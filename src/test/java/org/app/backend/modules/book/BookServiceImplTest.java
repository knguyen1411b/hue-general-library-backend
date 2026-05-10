package org.app.backend.modules.book;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.UUID;
import org.app.backend.common.exception.AppException;
import org.app.backend.core.file.FileService;
import org.app.backend.modules.audit.AuditLogService;
import org.app.backend.modules.audit.enums.AuditLogAction;
import org.app.backend.modules.audit.enums.AuditLogEntity;
import org.app.backend.modules.audit.enums.AuditLogStatus;
import org.app.backend.modules.auth.security.CustomUserDetails;
import org.app.backend.modules.book.dto.BookCreateDTO;
import org.app.backend.modules.book.dto.BookDTO;
import org.app.backend.modules.book.enums.BookStatus;
import org.app.backend.modules.bookItem.BookItem;
import org.app.backend.modules.bookItem.BookItemRepository;
import org.app.backend.modules.category.Category;
import org.app.backend.modules.category.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;

@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {

  @Mock private BookRepository bookRepository;
  @Mock private CategoryRepository categoryRepository;
  @Mock private FileService fileService;
  @Mock private ModelMapper modelMapper;
  @Mock private AuditLogService auditLogService;
  @Mock private BookItemRepository bookItemRepository;

  @Mock private TypeMap<BookCreateDTO, Book> typeMap;

  @InjectMocks private BookServiceImpl bookService;

  private Book mockBook;
  private CustomUserDetails mockUserDetails;
  private UUID bookId;
  private UUID categoryId;

  @BeforeEach
  void setUp() {
    bookId = UUID.randomUUID();
    categoryId = UUID.randomUUID();

    mockBook = new Book();
    mockBook.setId(bookId);
    mockBook.setTitle("Test Book");
    mockBook.setIsbn("123456789");
    mockBook.setStatus(BookStatus.ACTIVE);

    mockUserDetails = new CustomUserDetails();
    mockUserDetails.setId(UUID.randomUUID());
    mockUserDetails.setUsername("admin");
  }

  @Test
  @DisplayName("Find By Id - Success")
  void testFindById_Success() {
    BookDTO mockDto = new BookDTO();
    mockDto.setId(bookId);
    mockDto.setTitle("Test Book");

    when(bookRepository.findById(bookId)).thenReturn(Optional.of(mockBook));
    when(modelMapper.map(mockBook, BookDTO.class)).thenReturn(mockDto);

    BookDTO result = bookService.findById(bookId);

    assertNotNull(result);
    assertEquals("Test Book", result.getTitle());
    assertEquals(bookId, result.getId());
  }

  @Test
  @DisplayName("Find By Id - Not Found (Deleted status)")
  void testFindById_NotFoundDeleted() {
    mockBook.setStatus(BookStatus.DELETED);
    when(bookRepository.findById(bookId)).thenReturn(Optional.of(mockBook));

    AppException exception = assertThrows(AppException.class, () -> bookService.findById(bookId));
    assertEquals(BookMessage.NOT_FOUND.getMessage(), exception.getMessage());
  }

  @Test
  @DisplayName("Create Book - Success")
  void testCreateBook_Success() {
    BookCreateDTO dto = new BookCreateDTO();
    dto.setTitle("New Book");
    dto.setIsbn("987654321");
    dto.setCategoryId(categoryId);
    dto.setCount(1); // 1 book item

    Category mockCategory = new Category();
    mockCategory.setId(categoryId);

    when(bookRepository.existsByIsbn(dto.getIsbn())).thenReturn(false);
    when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(mockCategory));
    
    when(modelMapper.typeMap(BookCreateDTO.class, Book.class)).thenReturn(typeMap);
    when(typeMap.addMappings(any(org.modelmapper.ExpressionMap.class))).thenReturn(typeMap);
    
    Book mappedBook = new Book();
    mappedBook.setId(UUID.randomUUID()); // To be set to null in service
    mappedBook.setTitle("New Book");
    when(modelMapper.map(dto, Book.class)).thenReturn(mappedBook);
    
    when(bookItemRepository.existsByBarcode(anyString())).thenReturn(false);

    when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> {
      Book b = invocation.getArgument(0);
      b.setId(UUID.randomUUID());
      return b;
    });

    bookService.create(dto, mockUserDetails);

    verify(bookRepository, times(1)).save(mappedBook);
    verify(bookItemRepository, times(1)).save(any(BookItem.class));
    verify(auditLogService, times(1)).log(
        eq(mockUserDetails.getId()),
        eq(mockUserDetails.getUsername()),
        eq(AuditLogAction.CREATE),
        eq(AuditLogEntity.BOOK),
        anyString(),
        eq(AuditLogStatus.SUCCESS),
        anyString()
    );
    assertNotNull(mappedBook.getId());
    assertEquals(BookStatus.ACTIVE, mappedBook.getStatus());
  }

  @Test
  @DisplayName("Delete Book - Success")
  void testDeleteBook_Success() {
    when(bookRepository.findById(bookId)).thenReturn(Optional.of(mockBook));

    bookService.delete(bookId, mockUserDetails);

    assertEquals(BookStatus.DELETED, mockBook.getStatus());
    verify(bookRepository, times(1)).save(mockBook);
  }
}
