package org.app.backend.modules.book;

import java.util.UUID;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.app.backend.common.exception.AppException;
import org.app.backend.core.file.FileService;
import org.app.backend.modules.audit.AuditLogService;
import org.app.backend.modules.audit.enums.*;
import org.app.backend.modules.auth.security.CustomUserDetails;
import org.app.backend.modules.book.dto.*;
import org.app.backend.modules.book.filter.BookFilterDTO;
import org.app.backend.modules.category.Category;
import org.app.backend.modules.category.CategoryRepository;
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
public class BookServiceImpl implements BookService {
  BookRepository bookRepository;
  CategoryRepository categoryRepository;
  FileService fileService;
  ModelMapper modelMapper;
  AuditLogService auditLogService;

  @Override
  @Transactional(readOnly = true)
  public Page<BookDTO> findAll(BookFilterDTO filter, Pageable pageable) {
    Specification<Book> spec = BookSpecification.filter(filter);
    return bookRepository.findAll(spec, pageable).map(book -> modelMapper.map(book, BookDTO.class));
  }

  @Override
  @Transactional(readOnly = true)
  public BookDTO findById(UUID id) {
    return bookRepository
        .findById(id)
        .filter(b -> b.getStatus() != BookStatus.DELETED)
        .map(book -> modelMapper.map(book, BookDTO.class))
        .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Không tìm thấy đầu sách"));
  }

  @Override
  @Transactional
  @PreAuthorize("hasAnyRole('MANAGER')")
  public void create(@NonNull BookCreateDTO dto, CustomUserDetails actor) {
    validateIsbnAvailability(dto.getIsbn(), null);

    Category category =
        categoryRepository
            .findById(dto.getCategoryId())
            .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Danh mục không tồn tại"));

    // Configure ModelMapper to skip id for CREATE flow (prevent
    // ObjectOptimisticLockingFailureException)
    modelMapper
        .typeMap(BookCreateDTO.class, Book.class)
        .addMappings(mapper -> mapper.skip(Book::setId));

    Book book = modelMapper.map(dto, Book.class);
    // Ensure id is null for INSERT (not UPDATE)
    book.setId(null);
    book.setCategory(category);
    book.setStatus(BookStatus.ACTIVE);

    if (dto.getThumbnail() != null) {
      String url = fileService.upload(dto.getThumbnail(), "temp");
      book.setThumbnailUrl(url);
    }

    bookRepository.save(book);

    auditLogService.log(
        actor != null ? actor.getId() : null,
        actor != null ? actor.getUsername() : "system",
        AuditLogAction.CREATE,
        AuditLogEntity.BOOK,
        book.getId().toString(),
        AuditLogStatus.SUCCESS,
        "Thêm mới đầu sách thành công: " + book.getTitle());
  }

  @Override
  @Transactional
  @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
  public void update(UUID id, BookUpdateDTO dto, CustomUserDetails actor) {
    Book book =
        bookRepository
            .findById(id)
            .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Không tìm thấy đầu sách"));

    if (dto.getIsbn() != null) {
      validateIsbnAvailability(dto.getIsbn(), book.getIsbn());
    }

    if (dto.getCategoryId() != null) {
      Category category =
          categoryRepository
              .findById(dto.getCategoryId())
              .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Danh mục không tồn tại"));
      book.setCategory(category);
    }

    if (dto.getThumbnail() != null) {
      book.setThumbnailUrl(
          fileService.upload(dto.getThumbnail(), book.getId().toString() + "_thumb"));
    }

    modelMapper.map(dto, book);
    bookRepository.save(book);

    auditLogService.log(
        actor != null ? actor.getId() : null,
        actor != null ? actor.getUsername() : "system",
        AuditLogAction.UPDATE,
        AuditLogEntity.BOOK,
        book.getId().toString(),
        AuditLogStatus.SUCCESS,
        "Cập nhật đầu sách thành công: " + book.getTitle());
  }

  @Override
  @Transactional
  @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
  public void delete(UUID id, CustomUserDetails actor) {
    Book book =
        bookRepository
            .findById(id)
            .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Không tìm thấy đầu sách"));

    book.setStatus(BookStatus.DELETED);
    bookRepository.save(book);

    auditLogService.log(
        actor != null ? actor.getId() : null,
        actor != null ? actor.getUsername() : "system",
        AuditLogAction.DELETE,
        AuditLogEntity.BOOK,
        book.getId().toString(),
        AuditLogStatus.SUCCESS,
        "Xóa mềm đầu sách thành công: " + book.getTitle());
  }

  private void validateIsbnAvailability(String isbn, String currentIsbn) {
    if (isbn == null || isbn.isBlank() || isbn.equals(currentIsbn)) {
      return;
    }
    if (bookRepository.existsByIsbn(isbn)) {
      throw new AppException(HttpStatus.CONFLICT, "Mã ISBN đã tồn tại trên hệ thống");
    }
  }
}