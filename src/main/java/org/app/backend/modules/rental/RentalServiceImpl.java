package org.app.backend.modules.rental;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.app.backend.common.exception.AppException;
import org.app.backend.modules.audit.AuditLogService;
import org.app.backend.modules.audit.enums.AuditLogAction;
import org.app.backend.modules.audit.enums.AuditLogEntity;
import org.app.backend.modules.audit.enums.AuditLogStatus;
import org.app.backend.modules.auth.security.CustomUserDetails;
import org.app.backend.modules.bookItem.BookItem;
import org.app.backend.modules.bookItem.BookItemRepository;
import org.app.backend.modules.bookItem.enums.BookItemStatus;
import org.app.backend.modules.fine.Fine;
import org.app.backend.modules.fine.FineRepository;
import org.app.backend.modules.fine.enums.FineStatus;
import org.app.backend.modules.rental.dto.RentalCreateDTO;
import org.app.backend.modules.rental.dto.RentalPreviewDTO;
import org.app.backend.modules.rental.dto.RentalRenewDTO;
import org.app.backend.modules.rental.dto.RentalResponseDTO;
import org.app.backend.modules.rental.dto.RentalReturnDTO;
import org.app.backend.modules.rental.enums.BookCondition;
import org.app.backend.modules.rental.enums.RentalStatus;
import org.app.backend.modules.user.User;
import org.app.backend.modules.user.UserRepository;
import org.app.backend.modules.user.UserRole;
import org.app.backend.modules.usersubscription.UserSubscription;
import org.app.backend.modules.usersubscription.UserSubscriptionRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RentalServiceImpl implements RentalService {

  UserRepository userRepository;
  BookItemRepository bookItemRepository;
  RentalRepository rentalRepository;
  FineRepository fineRepository;
  UserSubscriptionRepository userSubscriptionRepository;
  AuditLogService auditLogService;

  ModelMapper modelMapper;

  // 1. MƯỢN SÁCH
  @Override
  @Transactional
  public void rentBooks(RentalCreateDTO dto, CustomUserDetails currentUser) {
    // Kiểm tra quyền: User chỉ có thể mượn cho chính mình, Librarian có thể mượn cho bất kỳ user
    // nào
    if (currentUser.getRole() == UserRole.USER) {
      if (!dto.getUserId().equals(currentUser.getId())) {
        throw new AppException(
            HttpStatus.FORBIDDEN, "Độc giả chỉ có thể mượn sách cho chính mình!");
      }
    } else if (currentUser.getRole() != UserRole.MANAGER) {
      throw new AppException(HttpStatus.FORBIDDEN, "Chỉ Thủ thư và Độc giả mới có thể mượn sách!");
    }

    User user =
        userRepository
            .findById(dto.getUserId())
            .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Độc giả không tồn tại"));

    UserSubscription activeSub =
        userSubscriptionRepository
            .findActiveSubscriptionByUserId(user.getId())
            .orElseThrow(
                () ->
                    new AppException(
                        HttpStatus.BAD_REQUEST, "Thẻ đã hết hạn. Yêu cầu độc giả gia hạn thẻ!"));

    if (fineRepository.existsByRental_User_IdAndStatus(user.getId(), FineStatus.UNPAID)) {
      throw new AppException(HttpStatus.FORBIDDEN, "Tài khoản đang nợ phí phạt. Từ chối cho mượn!");
    }

    int maxAllowed = activeSub.getSubscription().getMaxBooks();
    int currentBorrowingCount =
        rentalRepository.countByUser_IdAndStatus(user.getId(), RentalStatus.BORROWING);
    if (currentBorrowingCount + dto.getBarcodes().size() > maxAllowed) {
      throw new AppException(HttpStatus.BAD_REQUEST, "Vượt giới hạn sách cho phép của gói cước!");
    }

    int durationDays = activeSub.getSubscription().getDurationDays();
    Instant dueDate = Instant.now().plus(durationDays, ChronoUnit.DAYS);
    List<Rental> newRentals = new ArrayList<>();
    List<BookItem> updatedBookItems = new ArrayList<>();

    for (String barcode : dto.getBarcodes()) {
      BookItem item =
          bookItemRepository
              .findByBarcode(barcode)
              .orElseThrow(
                  () ->
                      new AppException(HttpStatus.NOT_FOUND, "Không tìm thấy mã vạch: " + barcode));

      if (item.getStatus() != BookItemStatus.AVAILABLE) {
        throw new AppException(HttpStatus.CONFLICT, "Sách hiện không có sẵn trong kho");
      }
      item.setStatus(BookItemStatus.BORROWED);
      updatedBookItems.add(item);

      newRentals.add(
          Rental.builder()
              .user(user)
              .bookItem(item)
              .status(RentalStatus.BORROWING)
              .dueDate(dueDate)
              .build());
    }

    bookItemRepository.saveAll(updatedBookItems);
    rentalRepository.saveAll(newRentals);

    // Ghi log audit dựa trên role
    String auditMessage;
    if (currentUser.getRole() == UserRole.MANAGER) {
      auditMessage =
          String.format(
              "Thủ thư %s tạo phiếu mượn %d sách cho độc giả %s tại thư viện",
              currentUser.getFullName(), dto.getBarcodes().size(), user.getFullName());
    } else {
      auditMessage =
          String.format(
              "Độc giả %s tự mượn %d sách trên hệ thống online",
              currentUser.getFullName(), dto.getBarcodes().size());
    }

    auditLogService.log(
        currentUser.getId(),
        currentUser.getUsername(),
        AuditLogAction.BORROW_CREATED,
        AuditLogEntity.RENTAL,
        user.getId().toString(),
        AuditLogStatus.SUCCESS,
        auditMessage);
  }

  // 2. XEM TRƯỚC THÔNG TIN TRẢ SÁCH
  @Override
  public RentalPreviewDTO getReturnPreview(String barcode) {
    Rental rental =
        rentalRepository
            .findByBookItem_BarcodeAndStatus(barcode, RentalStatus.BORROWING)
            .orElseThrow(
                () ->
                    new AppException(
                        HttpStatus.NOT_FOUND, "Sách không nằm trong trạng thái đang mượn"));

    User user = rental.getUser();
    UserSubscription activeSub =
        userSubscriptionRepository.findActiveSubscriptionByUserId(user.getId()).orElse(null);
    BookItem item = rental.getBookItem();

    boolean isOverdue = Instant.now().isAfter(rental.getDueDate());
    long overdueDays = isOverdue ? ChronoUnit.DAYS.between(rental.getDueDate(), Instant.now()) : 0;

    return RentalPreviewDTO.builder()
        .rentalId(rental.getId().toString())
        .readerName(user.getFullName())
        .readerPhone(user.getPhone())
        .subscriptionPackage(activeSub != null ? activeSub.getSubscription().getName() : "N/A")
        .borrowDate(rental.getRentDate())
        .dueDate(rental.getDueDate())
        .bookTitle(item.getBook().getTitle())
        .bookAuthor(item.getBook().getAuthor())
        .bookBarcode(barcode)
        .bookPrice(BigDecimal.valueOf(item.getBook().getPrice()))
        .isOverdue(isOverdue)
        .overdueDays(overdueDays)
        .displayDateTime(Instant.now())
        .build();
  }

  // 3. TRẢ SÁCH
  @Override
  @Transactional
  public void returnBooks(RentalReturnDTO dto, CustomUserDetails librarian) {
    // Chỉ Thủ thư mới có thể xử lý trả sách (trực tiếp tại thư viện)
    if (librarian == null || librarian.getRole() != UserRole.MANAGER) {
      throw new AppException(
          HttpStatus.FORBIDDEN,
          "Chỉ Thủ thư mới có thể xử lý trả sách tại thư viện. Độc giả vui lòng trả sách trực tiếp tại thư viện!");
    }

    if (!dto.isConfirmReturn()) {
      throw new AppException(HttpStatus.BAD_REQUEST, "Phải xác nhận trả sách");
    }

    List<BookItem> updatedBookItems = new ArrayList<>();
    List<Rental> updatedRentals = new ArrayList<>();
    List<Fine> newFines = new ArrayList<>();

    for (String barcode : dto.getBarcodes()) {
      Rental rental =
          rentalRepository
              .findByBookItem_BarcodeAndStatus(barcode, RentalStatus.BORROWING)
              .orElseThrow(
                  () ->
                      new AppException(
                          HttpStatus.NOT_FOUND, "Sách không nằm trong trạng thái đang mượn"));

      BookItem item = rental.getBookItem();
      User user = rental.getUser();
      UserSubscription activeSub =
          userSubscriptionRepository
              .findActiveSubscriptionByUserId(user.getId())
              .orElseThrow(
                  () ->
                      new AppException(
                          HttpStatus.BAD_REQUEST, "Người dùng không có gói cước hợp lệ"));
      var subscription = activeSub.getSubscription();

      rental.setReturnDate(Instant.now());

      // Xử lý theo tình trạng sách
      if (dto.getCondition() == BookCondition.LOST) {
        rental.setStatus(RentalStatus.LOST);
        item.setStatus(BookItemStatus.DISCARDED); // Thay vì LOST, dùng DISCARDED

        int compensationAmount =
            (int) (item.getBook().getPrice() * subscription.getCompensationRate() / 100.0);
        newFines.add(
            Fine.builder()
                .rental(rental)
                .amount(compensationAmount)
                .reason(
                    "Làm mất sách: "
                        + item.getBook().getTitle()
                        + " - bồi thường "
                        + subscription.getCompensationRate()
                        + "%")
                .status(FineStatus.UNPAID)
                .build());

        // Ghi log sách bị mất
        auditLogService.log(
            librarian != null ? librarian.getId() : null,
            librarian != null ? librarian.getUsername() : "system",
            AuditLogAction.ITEM_LOST,
            AuditLogEntity.RENTAL,
            rental.getId().toString(),
            AuditLogStatus.SUCCESS,
            "Độc giả làm mất sách: "
                + item.getBook().getTitle()
                + " (Mã: "
                + barcode
                + ") - Bồi thường: "
                + compensationAmount
                + " VND");
      } else if (dto.getCondition() == BookCondition.DAMAGED) {
        rental.setStatus(RentalStatus.RETURNED);
        item.setStatus(BookItemStatus.MAINTENANCE); // Gửi bảo trì

        // Ghi log kiểm tra cho sách hư hỏng
        auditLogService.log(
            librarian != null ? librarian.getId() : null,
            librarian != null ? librarian.getUsername() : "system",
            AuditLogAction.DAMAGE_DETECTED,
            AuditLogEntity.BOOK_ITEM,
            item.getId().toString(),
            AuditLogStatus.SUCCESS,
            "Phát hiện sách bị hư: "
                + (dto.getPhysicalConditionNotes() != null
                    ? dto.getPhysicalConditionNotes()
                    : "Không có ghi chú"));
      } else { // GOOD
        rental.setStatus(RentalStatus.RETURNED);
        item.setStatus(BookItemStatus.AVAILABLE);
      }

      // Xử lý quá hạn (áp dụng cho tất cả trường hợp trừ khi sách bị hư)
      if (dto.getCondition() != BookCondition.DAMAGED
          && Instant.now().isAfter(rental.getDueDate())) {
        long overdueDays = ChronoUnit.DAYS.between(rental.getDueDate(), Instant.now());
        int fineAmount = (int) (overdueDays * subscription.getOverdueFeePerDay());

        newFines.add(
            Fine.builder()
                .rental(rental)
                .amount(fineAmount)
                .reason(
                    String.format(
                        "Trả sách quá hạn %d ngày - Gói: %s", overdueDays, subscription.getName()))
                .status(FineStatus.UNPAID)
                .build());

        // Ghi log trả sách trễ hạn
        auditLogService.log(
            librarian != null ? librarian.getId() : null,
            librarian != null ? librarian.getUsername() : "system",
            AuditLogAction.OVERDUE_DETECTED,
            AuditLogEntity.RENTAL,
            rental.getId().toString(),
            AuditLogStatus.SUCCESS,
            String.format(
                "Sách trả trễ %d ngày - Phạt: %d VND - Độc giả: %s",
                overdueDays, fineAmount, user.getFullName()));

        if (dto.getCondition() == BookCondition.GOOD) {
          rental.setStatus(RentalStatus.OVERDUE);
        }
      }

      // Ghi log trả sách thành công nếu không có phạt
      if (newFines.isEmpty() && dto.getCondition() == BookCondition.GOOD) {
        auditLogService.log(
            librarian != null ? librarian.getId() : null,
            librarian != null ? librarian.getUsername() : "system",
            AuditLogAction.RETURN_SUCCESS,
            AuditLogEntity.RENTAL,
            rental.getId().toString(),
            AuditLogStatus.SUCCESS,
            "Trả sách thành công - Sách: "
                + item.getBook().getTitle()
                + " - Độc giả: "
                + user.getFullName());
      }

      updatedBookItems.add(item);
      updatedRentals.add(rental);
    }

    bookItemRepository.saveAll(updatedBookItems);
    rentalRepository.saveAll(updatedRentals);
    if (!newFines.isEmpty()) {
      fineRepository.saveAll(newFines);
      // TODO: Kích hoạt quy trình thanh toán
      triggerPaymentWorkflow(newFines);
    }

    // Ghi log chung cho hành động trả sách được xử lý bởi thủ thư
    auditLogService.log(
        librarian.getId(),
        librarian.getUsername(),
        AuditLogAction.RETURN_SUCCESS,
        AuditLogEntity.RENTAL,
        null,
        AuditLogStatus.SUCCESS,
        String.format(
            "Thủ thư %s xử lý trả %d sách tại thư viện",
            librarian.getFullName(), dto.getBarcodes().size()));
  }

  private void triggerPaymentWorkflow(List<Fine> fines) {
    // TODO: Triển khai logic kích hoạt thanh toán (ví dụ: gửi thông báo, tạo hóa đơn, v.v.)
    // Hiện tại chỉ log
    auditLogService.log(
        null,
        "system",
        AuditLogAction.PAYMENT_TRIGGERED,
        AuditLogEntity.FINE,
        null,
        AuditLogStatus.SUCCESS,
        "Kích hoạt thanh toán cho " + fines.size() + " khoản phạt");
  }

  // 4. GIA HẠN SÁCH
  @Override
  @Transactional
  public void renewBook(RentalRenewDTO dto, CustomUserDetails librarian) {
    Rental rental =
        rentalRepository
            .findByBookItem_BarcodeAndStatus(dto.getBarcode(), RentalStatus.BORROWING)
            .orElseThrow(
                () ->
                    new AppException(
                        HttpStatus.NOT_FOUND, "Sách không nằm trong trạng thái đang mượn"));

    User user = rental.getUser();

    // Check 1: Đã quá hạn thì không cho gia hạn, bắt buộc phải trả và nộp phạt
    if (Instant.now().isAfter(rental.getDueDate())) {
      throw new AppException(
          HttpStatus.BAD_REQUEST,
          "Sách đã quá hạn, không thể gia hạn. Vui lòng trả sách và nộp phạt!");
    }

    // Check 2: Tài khoản có đang nợ tiền phạt hệ thống không?
    if (fineRepository.existsByRental_User_IdAndStatus(user.getId(), FineStatus.UNPAID)) {
      throw new AppException(
          HttpStatus.FORBIDDEN, "Tài khoản đang nợ phí phạt. Không thể gia hạn sách!");
    }

    // Check 3: Lấy gói cước hiện tại để cộng thêm ngày
    UserSubscription activeSub =
        userSubscriptionRepository
            .findActiveSubscriptionByUserId(user.getId())
            .orElseThrow(
                () -> new AppException(HttpStatus.BAD_REQUEST, "Thẻ mượn sách đã hết hạn!"));

    int extraDays = activeSub.getSubscription().getDurationDays();

    // Cập nhật ngày hết hạn mới (Cộng dồn từ ngày hết hạn cũ)
    rental.setDueDate(rental.getDueDate().plus(extraDays, ChronoUnit.DAYS));
    rentalRepository.save(rental);

    auditLogService.log(
        librarian != null ? librarian.getId() : null,
        librarian != null ? librarian.getUsername() : "system",
        AuditLogAction.UPDATE,
        AuditLogEntity.RENTAL,
        rental.getId().toString(),
        AuditLogStatus.SUCCESS,
        "Gia hạn sách thành công: " + dto.getBarcode());
  }

  // 1. Hàm hỗ trợ Map Entity sang DTO
  private RentalResponseDTO mapToDTO(Rental rental) {
    // ModelMapper tự động map: id, dueDate, returnDate, status (vì trùng tên)
    RentalResponseDTO dto = modelMapper.map(rental, RentalResponseDTO.class);

    // Những trường lồng sâu hoặc khác tên thì mình set thủ công cho chắc chắn
    if (rental.getBookItem() != null && rental.getBookItem().getBook() != null) {
      dto.setBookTitle(rental.getBookItem().getBook().getTitle());
      dto.setBarcode(rental.getBookItem().getBarcode());
    }

    if (rental.getUser() != null) {
      dto.setReaderName(rental.getUser().getFullName());
    }

    // Lưu ý: Nếu trong DTO bạn đặt là borrowDate nhưng trong Entity là createdAt
    dto.setBorrowDate(rental.getRentDate());

    return dto;
  }

  // 2. API cho Độc giả xem lịch sử của mình
  @Override
  public Page<RentalResponseDTO> getMyRentals(UUID userId, Pageable pageable) {
    return rentalRepository.findByUser_IdOrderByDueDateDesc(userId, pageable).map(this::mapToDTO);
  }

  // 3. API cho Thủ thư xem toàn bộ
  @Override
  public Page<RentalResponseDTO> getAllRentals(Pageable pageable) {
    return rentalRepository.findAll(pageable).map(this::mapToDTO);
  }
}