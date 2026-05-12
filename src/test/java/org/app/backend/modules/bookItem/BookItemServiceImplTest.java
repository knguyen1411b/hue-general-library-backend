package org.app.backend.modules.bookItem;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.UUID;
import org.app.backend.common.exception.AppException;
import org.app.backend.modules.audit.AuditLogService;
import org.app.backend.modules.audit.enums.AuditLogAction;
import org.app.backend.modules.audit.enums.AuditLogEntity;
import org.app.backend.modules.audit.enums.AuditLogStatus;
import org.app.backend.modules.auth.security.CustomUserDetails;
import org.app.backend.modules.book.Book;
import org.app.backend.modules.book.BookRepository;
import org.app.backend.modules.bookItem.dto.BookItemCreateDTO;
import org.app.backend.modules.bookItem.dto.BookItemDTO;
import org.app.backend.modules.bookItem.enums.BookItemStatus;
import org.app.backend.modules.warehouse.entity.Position;
import org.app.backend.modules.warehouse.repository.PositionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

@ExtendWith(MockitoExtension.class)
class BookItemServiceImplTest {

  @Mock private BookItemRepository bookItemRepository;
  @Mock private BookRepository bookRepository;
  @Mock private ModelMapper modelMapper;
  @Mock private AuditLogService auditLogService;
  @Mock private PositionRepository positionRepository;

  @InjectMocks private BookItemServiceImpl bookItemService;

  private BookItem mockBookItem;
  private CustomUserDetails mockUserDetails;
  private UUID bookItemId;
  private UUID bookId;

  @BeforeEach
  void setUp() {
    bookItemId = UUID.randomUUID();
    bookId = UUID.randomUUID();

    Book mockBook = new Book();
    mockBook.setId(bookId);
    mockBook.setTitle("Mock Book");

    mockBookItem = new BookItem();
    mockBookItem.setId(bookItemId);
    mockBookItem.setBarcode("BC123456");
    mockBookItem.setBook(mockBook);
    mockBookItem.setStatus(BookItemStatus.AVAILABLE);

    mockUserDetails = new CustomUserDetails();
    mockUserDetails.setId(UUID.randomUUID());
    mockUserDetails.setUsername("admin");
  }

  @Test
  @DisplayName("Find By Id - Success")
  void testFindById_Success() {
    BookItemDTO mockDto = new BookItemDTO();
    mockDto.setId(bookItemId);
    mockDto.setBarcode("BC123456");

    when(bookItemRepository.findById(bookItemId)).thenReturn(Optional.of(mockBookItem));
    when(modelMapper.map(mockBookItem, BookItemDTO.class)).thenReturn(mockDto);

    BookItemDTO result = bookItemService.findById(bookItemId);

    assertNotNull(result);
    assertEquals("BC123456", result.getBarcode());
  }

  @Test
  @DisplayName("Find By Id - Not Found")
  void testFindById_NotFound() {
    when(bookItemRepository.findById(bookItemId)).thenReturn(Optional.empty());

    AppException exception =
        assertThrows(AppException.class, () -> bookItemService.findById(bookItemId));
    assertEquals(BookItemMessage.NOT_FOUND.getMessage(), exception.getMessage());
  }

  @Test
  @DisplayName("Create Book Item - Success")
  void testCreateBookItem_Success() {
    BookItemCreateDTO dto = new BookItemCreateDTO();
    dto.setBookId(bookId);
    dto.setBarcode("BC999999");
    dto.setShelfPositionId(UUID.randomUUID());

    Book mockBook = new Book();
    mockBook.setId(bookId);

    Position mockPosition = new Position();
    mockPosition.setId(dto.getShelfPositionId());

    when(bookRepository.findById(bookId)).thenReturn(Optional.of(mockBook));
    when(bookItemRepository.existsByBarcode(dto.getBarcode())).thenReturn(false);
    when(positionRepository.findById(dto.getShelfPositionId()))
        .thenReturn(Optional.of(mockPosition));

    when(bookItemRepository.save(any(BookItem.class)))
        .thenAnswer(
            invocation -> {
              BookItem item = invocation.getArgument(0);
              item.setId(UUID.randomUUID());
              return item;
            });

    bookItemService.create(dto, mockUserDetails);

    verify(bookItemRepository, times(1)).save(any(BookItem.class));
    verify(auditLogService, times(1))
        .log(
            eq(mockUserDetails.getId()),
            eq(mockUserDetails.getUsername()),
            eq(AuditLogAction.CREATE),
            eq(AuditLogEntity.BOOK_ITEM),
            anyString(), // Now it has an ID because of the mock
            eq(AuditLogStatus.SUCCESS),
            anyString());
  }

  @Test
  @DisplayName("Delete Book Item - Success")
  void testDeleteBookItem_Success() {
    when(bookItemRepository.findById(bookItemId)).thenReturn(Optional.of(mockBookItem));

    bookItemService.delete(bookItemId, mockUserDetails);

    assertEquals(BookItemStatus.DELETED, mockBookItem.getStatus());
    verify(bookItemRepository, times(1)).save(mockBookItem);
  }
}
