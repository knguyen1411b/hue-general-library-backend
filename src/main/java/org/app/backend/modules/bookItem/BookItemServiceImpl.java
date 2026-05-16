package org.app.backend.modules.bookItem;

import java.util.UUID;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.app.backend.common.exception.AppException;
import org.app.backend.modules.audit.AuditLogService;
import org.app.backend.modules.audit.enums.*;
import org.app.backend.modules.auth.security.CustomUserDetails;
import org.app.backend.modules.book.Book;
import org.app.backend.modules.book.BookRepository;
import org.app.backend.modules.bookItem.dto.*;
import org.app.backend.modules.bookItem.enums.BookItemStatus;
import org.app.backend.modules.bookItem.filter.BookItemFilterDTO;
import org.app.backend.modules.warehouse.WarehouseMessage;
import org.app.backend.modules.warehouse.entity.Aisle;
import org.app.backend.modules.warehouse.entity.Floor;
import org.app.backend.modules.warehouse.entity.Position;
import org.app.backend.modules.warehouse.entity.Shelf;
import org.app.backend.modules.warehouse.repository.PositionRepository;
import org.app.backend.modules.warehouse.repository.ShelfRepository;
import org.jspecify.annotations.NonNull;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookItemServiceImpl implements BookItemService {
  private static final String BARCODE_PATTERN = "^[A-Z0-9]{19}$";

  BookItemRepository bookItemRepository;
  BookRepository bookRepository;
  PositionRepository positionRepository;
  ShelfRepository shelfRepository;
  ModelMapper modelMapper;
  AuditLogService auditLogService;

  @Override
  @Transactional(readOnly = true)
  @PreAuthorize("hasAnyRole('ADMIN','MANAGER','USER')")
  public Page<BookItemDTO> findAll(BookItemFilterDTO filter, Pageable pageable) {
    Specification<BookItem> spec = BookItemSpecification.filter(filter);
    return bookItemRepository
        .findAll(spec, pageable)
        .map(
            bookItem -> {
              return mapBookItemDto(bookItem);
            });
  }

  @Override
  @Transactional(readOnly = true)
  @PreAuthorize("hasAnyRole('ADMIN','MANAGER','USER')")
  public BookItemDTO findById(UUID id) {
    BookItem bookItem =
        bookItemRepository
            .findById(id)
            .filter(item -> item.getStatus() != BookItemStatus.DELETED)
            .orElseThrow(
                () ->
                    new AppException(HttpStatus.NOT_FOUND, BookItemMessage.NOT_FOUND.getMessage()));
    return mapBookItemDto(bookItem);
  }

  @Override
  @Transactional
  @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
  public void create(@NonNull BookItemCreateDTO dto, CustomUserDetails actor) {
    validateBarcodeFormat(dto.getBarcode());
    validateBarcodeAvailability(dto.getBarcode());

    Book book =
        bookRepository
            .findById(dto.getBookId())
            .orElseThrow(
                () ->
                    new AppException(
                        HttpStatus.NOT_FOUND, BookItemMessage.BOOK_NOT_FOUND.getMessage()));

    BookItem bookItem = BookItem.builder().barcode(dto.getBarcode()).book(book).build();

    if (dto.getImportDate() != null) {
      bookItem.setImportDate(dto.getImportDate());
    }

    if (dto.getShelfPositionId() != null) {
      bookItem.setPosition(getOrCreateDefaultPositionForShelf(dto.getShelfPositionId()));
    }

    bookItemRepository.save(bookItem);
    auditLogService.log(
        actor != null ? actor.getId() : null,
        actor != null ? actor.getUsername() : "system",
        AuditLogAction.CREATE,
        AuditLogEntity.BOOK_ITEM,
        bookItem.getId().toString(),
        AuditLogStatus.SUCCESS,
        "Tạo sách thành công: " + bookItem.getBarcode());
  }

  @Override
  @Transactional
  @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
  public void update(UUID id, BookItemUpdateDTO dto, CustomUserDetails actor) {
    BookItem bookItem =
        bookItemRepository
            .findById(id)
            .filter(item -> item.getStatus() != BookItemStatus.DELETED)
            .orElseThrow(
                () ->
                    new AppException(HttpStatus.NOT_FOUND, BookItemMessage.NOT_FOUND.getMessage()));

    if (dto.getBarcode() != null) {
      validateBarcodeFormat(dto.getBarcode());
      validateBarcodeAvailability(dto.getBarcode(), bookItem.getBarcode());
      bookItem.setBarcode(dto.getBarcode());
    }

    if (dto.getImportDate() != null) {
      bookItem.setImportDate(dto.getImportDate());
    }

    if (dto.getStatus() != null) {
      validateStatusUpdate(bookItem, dto.getStatus());
      bookItem.setStatus(dto.getStatus());
    }

    if (dto.getShelfPositionId() != null) {
      bookItem.setPosition(getOrCreateDefaultPositionForShelf(dto.getShelfPositionId()));
    }

    bookItemRepository.save(bookItem);
    auditLogService.log(
        actor != null ? actor.getId() : null,
        actor != null ? actor.getUsername() : "system",
        AuditLogAction.UPDATE,
        AuditLogEntity.BOOK_ITEM,
        bookItem.getId().toString(),
        AuditLogStatus.SUCCESS,
        "Cập nhật sách thành công: " + bookItem.getBarcode());
  }

  @Override
  @Transactional
  @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
  public void delete(UUID id, CustomUserDetails actor) {
    BookItem bookItem =
        bookItemRepository
            .findById(id)
            .filter(item -> item.getStatus() != BookItemStatus.DELETED)
            .orElseThrow(
                () ->
                    new AppException(HttpStatus.NOT_FOUND, BookItemMessage.NOT_FOUND.getMessage()));
    if (bookItem.getStatus() == BookItemStatus.BORROWED) {
      throw new AppException(HttpStatus.CONFLICT, "Ban sach dang duoc muon, khong the xoa");
    }
    auditLogService.log(
        actor != null ? actor.getId() : null,
        actor != null ? actor.getUsername() : "system",
        AuditLogAction.DELETE,
        AuditLogEntity.BOOK_ITEM,
        bookItem.getId().toString(),
        AuditLogStatus.SUCCESS,
        "Xóa sách thành công: " + bookItem.getBarcode());
    bookItem.setStatus(BookItemStatus.DELETED);
    bookItemRepository.save(bookItem);
  }

  private BookItemDTO mapBookItemDto(BookItem bookItem) {
    BookItemDTO dto = modelMapper.map(bookItem, BookItemDTO.class);

    if (bookItem.getBook() != null) {
      dto.setBookId(bookItem.getBook().getId());
      dto.setBookTitle(bookItem.getBook().getTitle());
    }

    Position position = bookItem.getPosition();
    if (position != null) {
      Shelf shelf = position.getShelf();
      if (shelf != null) {
        dto.setShelfPositionId(shelf.getId());
        dto.setShelfName(shelf.getName());

        Aisle aisle = shelf.getAisle();
        if (aisle != null) {
          dto.setAisleName(aisle.getName());

          Floor floor = aisle.getFloor();
          if (floor != null) {
            dto.setFloorName(floor.getName());
          }
        }
      }
    }

    return dto;
  }

  private void validateBarcodeFormat(String barcode) {
    if (barcode == null || !barcode.matches(BARCODE_PATTERN)) {
      throw new AppException(
          HttpStatus.BAD_REQUEST, "Barcode phai la chuoi 19 ky tu chu cai in hoa hoac chu so");
    }
  }

  private Position getOrCreateDefaultPositionForShelf(UUID shelfId) {
    Shelf shelf =
        shelfRepository
            .findWithHierarchyById(shelfId) // dùng method có EntityGraph
            .orElseThrow(
                () ->
                    new AppException( // không tìm thấy → throw, KHÔNG fallback
                        HttpStatus.NOT_FOUND, WarehouseMessage.SHELF_NOT_FOUND.getMessage()));

    return getOrCreateDefaultPositionForShelf(shelf);
  }

  private Position getOrCreateDefaultPositionForShelf(Shelf shelf) {
    validateShelfHierarchy(shelf);

    return positionRepository
        .findFirstByShelfId(shelf.getId())
        .orElseGet(
            () -> {
              Position position = new Position();
              position.setShelf(shelf);
              return positionRepository.save(position);
            });
  }

  private Position getValidPositionOrThrow(UUID positionId) {
    Position position =
        positionRepository
            .findWithHierarchyById(positionId)
            .orElseThrow(
                () ->
                    new AppException(
                        HttpStatus.NOT_FOUND,
                        BookItemMessage.SHELF_POSITION_NOT_FOUND.getMessage()));
    if (position.getShelf() == null) {
      throw new AppException(
          HttpStatus.BAD_REQUEST, BookItemMessage.INVALID_SHELF_POSITION.getMessage());
    }
    validateShelfHierarchy(position.getShelf());
    return position;
  }

  private void validateShelfHierarchy(Shelf shelf) {
    if (shelf.getAisle() == null || shelf.getAisle().getFloor() == null) {
      throw new AppException(
          HttpStatus.BAD_REQUEST, BookItemMessage.INVALID_SHELF_POSITION.getMessage());
    }
  }

  private void validateStatusUpdate(BookItem bookItem, BookItemStatus newStatus) {
    if (newStatus == BookItemStatus.DELETED) {
      throw new AppException(HttpStatus.CONFLICT, "Khong duoc xoa ban sach bang PATCH");
    }
    if (bookItem.getStatus() == BookItemStatus.BORROWED && newStatus != BookItemStatus.BORROWED) {
      throw new AppException(
          HttpStatus.CONFLICT, "Ban sach dang duoc muon, khong the doi trang thai");
    }
  }

  private void validateBarcodeAvailability(String barcode) {
    if (bookItemRepository.existsByBarcode(barcode)) {
      throw new AppException(HttpStatus.CONFLICT, BookItemMessage.BARCODE_TAKEN.getMessage());
    }
  }

  private void validateBarcodeAvailability(String barcode, String currentBarcode) {
    if (barcode == null || barcode.isBlank() || barcode.equals(currentBarcode)) {
      return;
    }

    if (bookItemRepository.existsByBarcode(barcode)) {
      throw new AppException(HttpStatus.CONFLICT, BookItemMessage.BARCODE_TAKEN.getMessage());
    }
  }
}
