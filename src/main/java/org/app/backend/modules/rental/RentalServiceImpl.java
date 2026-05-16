package org.app.backend.modules.rental;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.app.backend.modules.auth.security.CustomUserDetails;
import org.app.backend.modules.book.Book;
import org.app.backend.modules.book.BookRepository;
import org.app.backend.modules.bookItem.BookItem;
import org.app.backend.modules.bookItem.BookItemRepository;
import org.app.backend.modules.bookItem.enums.BookItemStatus;
import org.app.backend.modules.fine.Fine;
import org.app.backend.modules.fine.FineRepository;
import org.app.backend.modules.fine.enums.FineStatus;
import org.app.backend.modules.rental.dto.RentalCreateDTO;
import org.app.backend.modules.rental.dto.RentalDTO;
import org.app.backend.modules.rental.enums.RentalStatus;
import org.app.backend.modules.usersubscription.UserSubscription;
import org.app.backend.modules.usersubscription.UserSubscriptionRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RentalServiceImpl implements RentalService {

  RentalRepository rentalRepository;
  ModelMapper modelMapper;
  UserSubscriptionRepository userSubscriptionRepository;
  BookItemRepository bookItemRepository;
  BookRepository bookRepository;
  FineRepository fineRepository;

  @Override
  @Transactional(readOnly = true)
  public Page<RentalDTO> findAll(
      Pageable pageable, UUID userId, RentalStatus status, UUID bookItemId) {
    if (userId != null) {
      return rentalRepository
          .findByUserId(userId, pageable)
          .map(r -> modelMapper.map(r, RentalDTO.class));
    }
    if (status != null) {
      return rentalRepository
          .findByStatus(status, pageable)
          .map(r -> modelMapper.map(r, RentalDTO.class));
    }
    if (bookItemId != null) {
      return rentalRepository
          .findByBookItemId(bookItemId, pageable)
          .map(r -> modelMapper.map(r, RentalDTO.class));
    }
    return rentalRepository.findAll(pageable).map(r -> modelMapper.map(r, RentalDTO.class));
  }

  @Override
  @Transactional(readOnly = true)
  public RentalDTO findById(UUID id) {
    Rental rental =
        rentalRepository.findById(id).orElseThrow(() -> new RuntimeException("Rental not found"));
    return modelMapper.map(rental, RentalDTO.class);
  }

  @Override
  @Transactional
  public RentalDTO create(RentalCreateDTO dto, CustomUserDetails actor) {
    // 1. Check user subscription
    UserSubscription activeSub =
        userSubscriptionRepository
            .findActiveSubscriptionByUserId(dto.getUserId())
            .orElseThrow(
                () ->
                    new RuntimeException(
                        "Không có gói cước hợp lệ hoặc hết hạn. Vui lòng gia hạn thẻ!"));

    // 2. Check user có fine chưa thanh toán không
    boolean hasUnpaidFine =
        fineRepository.existsByRental_UserIdAndStatus(dto.getUserId(), FineStatus.UNPAID);
    if (hasUnpaidFine) {
      throw new RuntimeException(
          "Tài khoản đang nợ phí phạt. Không thể mượn thêm sách cho đến khi thanh toán!");
    }

    // 3. Check số lượng sách hiện tại user đang mượn
    int maxBooksAllowed = activeSub.getSubscription().getMaxBooks();
    long currentBorrowingCount =
        rentalRepository.findByUserId(dto.getUserId()).stream()
            .filter(r -> r.getStatus() == RentalStatus.BORROWING)
            .count();
    if (currentBorrowingCount >= maxBooksAllowed) {
      throw new RuntimeException(
          "Bạn đã mượn tối đa " + maxBooksAllowed + " sách. Vui lòng trả sách để mượn thêm!");
    }

    // 4. Check BookItem status
    BookItem bookItem =
        bookItemRepository
            .findById(dto.getBookItemId())
            .orElseThrow(() -> new RuntimeException("Không tìm thấy bản sách (BookItem)!"));
    if (bookItem.getStatus() != BookItemStatus.AVAILABLE) {
      throw new RuntimeException(
          "Bản sách này không có sẵn (Trạng thái: " + bookItem.getStatus() + ")");
    }

    // 5. Update BookItem status to BORROWED
    bookItem.setStatus(BookItemStatus.BORROWED);
    bookItemRepository.save(bookItem);

    // 6. Decrease Book available count
    Book book = bookItem.getBook();

    // Giảm số lượng atomic
    int updatedRows = bookRepository.decreaseCount(book.getId());

    if (updatedRows == 0) {
      throw new RuntimeException("Không đủ sách có sẵn trong kho!");
    }

    // 7. Create rental record
    Rental rental = modelMapper.map(dto, Rental.class);
    rental.setId(null);
    rental.setStatus(RentalStatus.BORROWING);
    rental.setRentDate(LocalDate.now());
    Rental saved = rentalRepository.save(rental);

    return modelMapper.map(saved, RentalDTO.class);
  }

  @Override
  @Transactional
  public RentalDTO returnBook(UUID id, CustomUserDetails actor) {
    Rental rental =
        rentalRepository.findById(id).orElseThrow(() -> new RuntimeException("Rental not found"));

    LocalDate today = LocalDate.now();
    rental.setReturnDate(today);

    // 1. Get BookItem and restore status
    BookItem bookItem =
        bookItemRepository
            .findById(rental.getBookItemId())
            .orElseThrow(() -> new RuntimeException("BookItem không tìm thấy!"));

    // 2. Restore Book available count
    Book book = bookItem.getBook();
    bookRepository.increaseCount(book.getId());
    bookRepository.save(book);

    // 3. Check overdue and create fine if needed
    if (today.isAfter(rental.getDueDate())) {
      long overdueDays = ChronoUnit.DAYS.between(rental.getDueDate(), today);
      UserSubscription activeSub =
          userSubscriptionRepository
              .findActiveSubscriptionByUserId(rental.getUserId())
              .orElse(null);

      if (activeSub != null) {
        int overdueFeePerDay = activeSub.getSubscription().getOverdueFeePerDay();
        int totalFine = (int) (overdueDays * overdueFeePerDay);
        String reason = "Phạt trễ hạn " + overdueDays + " ngày tại " + overdueFeePerDay + "đ/ngày";

        // FIX: Check fine đã tồn tại chưa trước khi tạo mới
        boolean fineExists = fineRepository.existsByRental_Id(rental.getId());
        if (!fineExists) {
          Fine fine =
              Fine.builder()
                  .rental(rental)
                  .amount(totalFine)
                  .reason(reason)
                  .status(FineStatus.UNPAID)
                  .build();
          fineRepository.save(fine);
        } else {
          Fine existingFine =
              fineRepository
                  .findByRental_Id(rental.getId())
                  .orElseThrow(() -> new RuntimeException("Fine not found"));
          existingFine.setAmount(totalFine);
          existingFine.setReason(reason);
          fineRepository.save(existingFine);
        }

        rental.setStatus(RentalStatus.OVERDUE);
      }
      bookItem.setStatus(BookItemStatus.AVAILABLE);
      bookItemRepository.save(bookItem);
    } else {
      bookItem.setStatus(BookItemStatus.AVAILABLE);
      bookItemRepository.save(bookItem);
      rental.setStatus(RentalStatus.RETURNED);
    }

    Rental saved = rentalRepository.save(rental);
    return modelMapper.map(saved, RentalDTO.class);
  }

  @Override
  @Transactional
  public RentalDTO renewBook(UUID id, CustomUserDetails actor) {
    Rental rental =
        rentalRepository.findById(id).orElseThrow(() -> new RuntimeException("Rental not found"));

    // 1. Check user không có fine chưa thanh toán
    boolean hasUnpaidFine =
        fineRepository.existsByRental_UserIdAndStatus(rental.getUserId(), FineStatus.UNPAID);
    if (hasUnpaidFine) {
      throw new RuntimeException(
          "Tài khoản đang nợ phí phạt. Không thể gia hạn sách cho đến khi thanh toán!");
    }

    // 2. Check sách chưa quá hạn
    if (LocalDate.now().isAfter(rental.getDueDate())) {
      throw new RuntimeException(
          "Sách đã quá hạn. Vui lòng trả sách và thanh toán phạt trước khi gia hạn!");
    }

    // 3. Get subscription to extend due date
    UserSubscription activeSub =
        userSubscriptionRepository
            .findActiveSubscriptionByUserId(rental.getUserId())
            .orElseThrow(
                () -> new RuntimeException("Không tìm thấy gói cước hợp lệ cho người dùng!"));

    // 4. Extend due date by subscription duration
    int durationDays = activeSub.getSubscription().getDurationDays();
    rental.setDueDate(rental.getDueDate().plusDays(durationDays));

    Rental saved = rentalRepository.save(rental);
    return modelMapper.map(saved, RentalDTO.class);
  }

  @Override
  @Transactional
  public RentalDTO reportLost(UUID id, CustomUserDetails actor) {
    Rental rental =
        rentalRepository.findById(id).orElseThrow(() -> new RuntimeException("Rental not found"));

    // 1. Get BookItem and change status to DISCARDED
    BookItem bookItem =
        bookItemRepository
            .findById(rental.getBookItemId())
            .orElseThrow(() -> new RuntimeException("BookItem không tìm thấy!"));
    bookItem.setStatus(BookItemStatus.DISCARDED);
    bookItemRepository.save(bookItem);

    // 2. Get subscription for compensation fee
    UserSubscription activeSub =
        userSubscriptionRepository.findActiveSubscriptionByUserId(rental.getUserId()).orElse(null);

    // 3. Create fine for lost book compensation
    if (activeSub != null && bookItem.getBook() != null) {
      // FIX: Check fine đã tồn tại chưa trước khi tạo mới
      boolean fineExists = fineRepository.existsByRental_Id(rental.getId());
      if (!fineExists) {
        int compensationRate = activeSub.getSubscription().getCompensationRate();
        int compensationAmount = (int) (bookItem.getBook().getPrice() * compensationRate / 100.0);

        Fine fine =
            Fine.builder()
                .rental(rental)
                .amount(compensationAmount)
                .reason(
                    "Bồi thường sách mất: "
                        + bookItem.getBook().getTitle()
                        + " - "
                        + compensationRate
                        + "%")
                .status(FineStatus.UNPAID)
                .build();
        fineRepository.save(fine);
      }
    }

    rental.setStatus(RentalStatus.LOST);
    Rental saved = rentalRepository.save(rental);
    return modelMapper.map(saved, RentalDTO.class);
  }

  @Override
  @Transactional
  public void delete(UUID id, CustomUserDetails actor) {
    Rental rental =
        rentalRepository.findById(id).orElseThrow(() -> new RuntimeException("Rental not found"));
    rentalRepository.delete(rental);
  }
}
