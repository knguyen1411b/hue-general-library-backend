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
import org.app.backend.modules.bookItem.filter.BookItemFilterDTO;
import org.app.backend.modules.warehouse.entity.Position;
import org.app.backend.modules.warehouse.repository.PositionRepository;
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
  BookItemRepository bookItemRepository;
  BookRepository bookRepository;
  PositionRepository positionRepository;
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
              BookItemDTO dto = modelMapper.map(bookItem, BookItemDTO.class);
              if (bookItem.getBook() != null) {
                dto.setBookTitle(bookItem.getBook().getTitle());
              }
              if (bookItem.getPosition() != null) {
                dto.setShelfPositionId(bookItem.getPosition().getId());
              }
              return dto;
            });
  }

  @Override
  @Transactional(readOnly = true)
  @PreAuthorize("hasAnyRole('ADMIN','MANAGER','USER')")
  public BookItemDTO findById(UUID id) {
    BookItem bookItem =
        bookItemRepository
            .findById(id)
            .orElseThrow(
                () ->
                    new AppException(HttpStatus.NOT_FOUND, BookItemMessage.NOT_FOUND.getMessage()));
    BookItemDTO dto = modelMapper.map(bookItem, BookItemDTO.class);
    if (bookItem.getBook() != null) {
      dto.setBookTitle(bookItem.getBook().getTitle());
    }
    if (bookItem.getPosition() != null) {
      dto.setShelfPositionId(bookItem.getPosition().getId());
    }
    return dto;
  }

  @Override
  @Transactional
  @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
  public void create(@NonNull BookItemCreateDTO dto, CustomUserDetails actor) {
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
      Position position =
          positionRepository
              .findById(dto.getShelfPositionId())
              .orElseThrow(
                  () ->
                      new AppException(
                          HttpStatus.NOT_FOUND,
                          BookItemMessage.SHELF_POSITION_NOT_FOUND.getMessage()));
      bookItem.setPosition(position);
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
            .orElseThrow(
                () ->
                    new AppException(HttpStatus.NOT_FOUND, BookItemMessage.NOT_FOUND.getMessage()));

    if (dto.getBarcode() != null && !dto.getBarcode().isBlank()) {
      validateBarcodeAvailability(dto.getBarcode(), bookItem.getBarcode());
      bookItem.setBarcode(dto.getBarcode());
    }

    if (dto.getImportDate() != null) {
      bookItem.setImportDate(dto.getImportDate());
    }

    if (dto.getStatus() != null) {
      bookItem.setStatus(dto.getStatus());
    }

    if (dto.getShelfPositionId() != null) {
      Position position =
          positionRepository
              .findById(dto.getShelfPositionId())
              .orElseThrow(
                  () ->
                      new AppException(
                          HttpStatus.NOT_FOUND,
                          BookItemMessage.SHELF_POSITION_NOT_FOUND.getMessage()));
      bookItem.setPosition(position);
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
            .orElseThrow(
                () ->
                    new AppException(HttpStatus.NOT_FOUND, BookItemMessage.NOT_FOUND.getMessage()));
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
