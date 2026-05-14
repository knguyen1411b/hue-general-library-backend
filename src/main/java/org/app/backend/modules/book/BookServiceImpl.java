package org.app.backend.modules.book;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
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
import org.app.backend.modules.book.enums.BookStatus;
import org.app.backend.modules.book.utils.BookUtil;
import org.app.backend.modules.bookItem.BookItem;
import org.app.backend.modules.bookItem.BookItemRepository;
import org.app.backend.modules.bookItem.enums.BookItemStatus;
import org.app.backend.modules.category.Category;
import org.app.backend.modules.category.CategoryRepository;
import org.jspecify.annotations.NonNull;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookServiceImpl implements BookService {
  private static final Set<String> ALLOWED_SORT_PROPERTIES =
      Set.of(
          "title",
          "author",
          "isbn",
          "price",
          "publishedYear",
          "publishers",
          "count",
          "status",
          "createdAt",
          "updatedAt");

  BookRepository bookRepository;
  BookItemRepository bookItemRepository;
  CategoryRepository categoryRepository;
  FileService fileService;
  ModelMapper modelMapper;
  AuditLogService auditLogService;

  @Override
  @Transactional(readOnly = true)
  public Page<BookDTO> findAll(BookFilterDTO filter, Pageable pageable) {
    Specification<Book> spec = BookSpecification.filter(filter);
    return bookRepository
        .findAll(spec, sanitizePageable(pageable))
        .map(book -> modelMapper.map(book, BookDTO.class));
  }

  @Override
  @Transactional(readOnly = true)
  public BookDTO findById(UUID id) {
    return bookRepository
        .findById(id)
        .filter(b -> b.getStatus() != BookStatus.DELETED)
        .map(book -> modelMapper.map(book, BookDTO.class))
        .orElseThrow(
            () -> new AppException(HttpStatus.NOT_FOUND, BookMessage.NOT_FOUND.getMessage()));
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

    modelMapper
        .typeMap(BookCreateDTO.class, Book.class)
        .addMappings(mapper -> mapper.skip(Book::setId));

    Book book = modelMapper.map(dto, Book.class);
    book.setId(null);
    book.setCategory(category);
    book.setStatus(BookStatus.ACTIVE);

    if (dto.getThumbnail() != null) {
      String url = fileService.upload(dto.getThumbnail(), "temp");
      book.setThumbnailUrl(url);
    }

    bookRepository.save(book);
    createBookItems(book, book.getCount());

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

    if (dto.getTitle() != null) {
      book.setTitle(dto.getTitle());
    }
    if (dto.getDescription() != null) {
      book.setDescription(dto.getDescription());
    }
    if (dto.getPrice() != null) {
      book.setPrice(dto.getPrice());
    }
    if (dto.getAuthor() != null) {
      book.setAuthor(dto.getAuthor());
    }
    if (dto.getPublishers() != null) {
      book.setPublishers(dto.getPublishers());
    }
    if (dto.getCount() != null) {
      book.setCount(dto.getCount());
    }
    if (dto.getPublishedYear() != null) {
      book.setPublishedYear(dto.getPublishedYear());
    }
    if (dto.getStatus() != null) {
      book.setStatus(dto.getStatus());
    }
    bookRepository.save(book);
    if (dto.getCount() != null) {
      createBookItems(book, dto.getCount());
    }

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

  private void createBookItems(Book book, Integer quantity) {
    if (quantity == null || quantity <= 0) {
      return;
    }

    List<BookItem> bookItems = new ArrayList<>();
    for (int i = 0; i < quantity; i++) {
      bookItems.add(
          BookItem.builder()
              .book(book)
              .barcode(generateUniqueBarcode())
              .importDate(LocalDate.now())
              .status(BookItemStatus.AVAILABLE)
              .build());
    }
    bookItemRepository.saveAll(bookItems);
  }

  private String generateUniqueBarcode() {
    String barcode;
    do {
      barcode = BookUtil.generateBarcode();
    } while (bookItemRepository.existsByBarcode(barcode));
    return barcode;
  }

  private Pageable sanitizePageable(Pageable pageable) {
    if (pageable == null || pageable.isUnpaged()) {
      return pageable;
    }

    Sort safeSort =
        Sort.by(
            pageable.getSort().stream()
                .filter(order -> ALLOWED_SORT_PROPERTIES.contains(order.getProperty()))
                .toList());

    return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), safeSort);
  }
}
