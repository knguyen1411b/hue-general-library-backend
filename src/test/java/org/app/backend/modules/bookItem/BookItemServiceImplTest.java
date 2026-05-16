package org.app.backend.modules.bookItem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.app.backend.common.exception.AppException;
import org.app.backend.modules.audit.AuditLogService;
import org.app.backend.modules.auth.security.CustomUserDetails;
import org.app.backend.modules.book.Book;
import org.app.backend.modules.book.BookRepository;
import org.app.backend.modules.bookItem.dto.BookItemDTO;
import org.app.backend.modules.bookItem.dto.BookItemUpdateDTO;
import org.app.backend.modules.bookItem.enums.BookItemStatus;
import org.app.backend.modules.warehouse.entity.Aisle;
import org.app.backend.modules.warehouse.entity.Floor;
import org.app.backend.modules.warehouse.entity.Position;
import org.app.backend.modules.warehouse.entity.Shelf;
import org.app.backend.modules.warehouse.repository.PositionRepository;
import org.app.backend.modules.warehouse.repository.ShelfRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
class BookItemServiceImplTest {

  @Mock private BookItemRepository bookItemRepository;
  @Mock private BookRepository bookRepository;
  @Mock private ModelMapper modelMapper;
  @Mock private AuditLogService auditLogService;
  @Mock private PositionRepository positionRepository;
  @Mock private ShelfRepository shelfRepository;

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
    mockBookItem.setBarcode("ABCDEFGHIJKLMNOPQRS");
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
    mockDto.setBarcode("ABCDEFGHIJKLMNOPQRS");

    when(bookItemRepository.findById(bookItemId)).thenReturn(Optional.of(mockBookItem));
    when(modelMapper.map(mockBookItem, BookItemDTO.class)).thenReturn(mockDto);

    BookItemDTO result = bookItemService.findById(bookItemId);

    assertNotNull(result);
    assertEquals("ABCDEFGHIJKLMNOPQRS", result.getBarcode());
  }

  @Test
  @DisplayName("Find All - Maps book and warehouse position fields")
  void testFindAll_MapsBookAndPositionFields() {
    Position position = createPositionTree();
    mockBookItem.setPosition(position);
    BookItemDTO mappedDto = new BookItemDTO();

    when(bookItemRepository.findAll(
            org.mockito.ArgumentMatchers.<Specification<BookItem>>any(), eq(PageRequest.of(0, 10))))
        .thenReturn(new PageImpl<>(List.of(mockBookItem)));
    when(modelMapper.map(mockBookItem, BookItemDTO.class)).thenReturn(mappedDto);

    Page<BookItemDTO> result = bookItemService.findAll(null, PageRequest.of(0, 10));
    BookItemDTO dto = result.getContent().get(0);

    assertEquals(bookId, dto.getBookId());
    assertEquals("Mock Book", dto.getBookTitle());
    assertEquals(position.getShelf().getId(), dto.getShelfPositionId());
    assertEquals("Floor 1", dto.getFloorName());
    assertEquals("Aisle A", dto.getAisleName());
    assertEquals("Shelf 1", dto.getShelfName());
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
  @DisplayName("Find By Id - Deleted Item Not Found")
  void testFindById_DeletedItemNotFound() {
    mockBookItem.setStatus(BookItemStatus.DELETED);
    when(bookItemRepository.findById(bookItemId)).thenReturn(Optional.of(mockBookItem));

    AppException exception =
        assertThrows(AppException.class, () -> bookItemService.findById(bookItemId));
    assertEquals(BookItemMessage.NOT_FOUND.getMessage(), exception.getMessage());
  }

  // @Test
  // @DisplayName("Create Book Item - Success")
  // void testCreateBookItem_Success() {
  // BookItemCreateDTO dto = new BookItemCreateDTO();
  // dto.setBookId(bookId);
  // dto.setBarcode("ZYXWVUTSRQPONMLKJIH");
  // dto.setShelfPositionId(UUID.randomUUID());

  // Book mockBook = new Book();
  // mockBook.setId(bookId);

  // Position mockPosition = createPositionTree();
  // mockPosition.getShelf().setId(dto.getShelfPositionId());

  // when(bookRepository.findById(bookId)).thenReturn(Optional.of(mockBook));
  // when(bookItemRepository.existsByBarcode(dto.getBarcode())).thenReturn(false);
  // when(shelfRepository.findById(dto.getShelfPositionId()))
  // .thenReturn(Optional.of(mockPosition.getShelf()));
  // when(positionRepository.findFirstByShelfId(dto.getShelfPositionId()))
  // .thenReturn(Optional.of(mockPosition));

  // when(bookItemRepository.save(any(BookItem.class)))
  // .thenAnswer(
  // invocation -> {
  // BookItem item = invocation.getArgument(0);
  // item.setId(UUID.randomUUID());
  // return item;
  // });

  // bookItemService.create(dto, mockUserDetails);

  // verify(bookItemRepository, times(1)).save(any(BookItem.class));
  // verify(auditLogService, times(1))
  // .log(
  // eq(mockUserDetails.getId()),
  // eq(mockUserDetails.getUsername()),
  // eq(AuditLogAction.CREATE),
  // eq(AuditLogEntity.BOOK_ITEM),
  // anyString(), // Now it has an ID because of the mock
  // eq(AuditLogStatus.SUCCESS),
  // anyString());
  // }

  // @Test
  // @DisplayName("Create Book Item - Shelf Not Found")
  // void testCreateBookItem_ShelfNotFound() {
  // BookItemCreateDTO dto = new BookItemCreateDTO();
  // dto.setBookId(bookId);
  // dto.setBarcode("ZYXWVUTSRQPONMLKJIH");
  // dto.setShelfPositionId(UUID.randomUUID());

  // Book mockBook = new Book();
  // mockBook.setId(bookId);

  // when(bookItemRepository.existsByBarcode(dto.getBarcode())).thenReturn(false);
  // when(bookRepository.findById(bookId)).thenReturn(Optional.of(mockBook));
  // when(shelfRepository.findById(dto.getShelfPositionId())).thenReturn(Optional.empty());

  // assertThrows(AppException.class, () -> bookItemService.create(dto,
  // mockUserDetails));
  // verify(bookItemRepository, never()).save(any(BookItem.class));
  // }

  // @Test
  // @DisplayName("Update Book Item - Partial Status Only")
  // void testUpdateBookItem_PartialStatusOnly() {
  // BookItemUpdateDTO dto = new BookItemUpdateDTO();
  // dto.setStatus(BookItemStatus.MAINTENANCE);

  // when(bookItemRepository.findById(bookItemId)).thenReturn(Optional.of(mockBookItem));

  // bookItemService.update(bookItemId, dto, mockUserDetails);

  // assertEquals(BookItemStatus.MAINTENANCE, mockBookItem.getStatus());
  // verify(bookItemRepository, times(1)).save(mockBookItem);
  // }

  // @Test
  // @DisplayName("Update Book Item - Partial Shelf Position Only")
  // void testUpdateBookItem_PartialShelfPositionOnly() {
  // BookItemUpdateDTO dto = new BookItemUpdateDTO();
  // dto.setShelfPositionId(UUID.randomUUID());
  // Position position = createPositionTree();
  // position.getShelf().setId(dto.getShelfPositionId());

  // when(bookItemRepository.findById(bookItemId)).thenReturn(Optional.of(mockBookItem));
  // when(shelfRepository.findById(dto.getShelfPositionId()))
  // .thenReturn(Optional.of(position.getShelf()));
  // when(positionRepository.findFirstByShelfId(dto.getShelfPositionId()))
  // .thenReturn(Optional.of(position));

  // bookItemService.update(bookItemId, dto, mockUserDetails);

  // assertEquals(position, mockBookItem.getPosition());
  // verify(bookItemRepository, times(1)).save(mockBookItem);
  // }

  @Test
  @DisplayName("Update Book Item - Duplicate Barcode Conflict")
  void testUpdateBookItem_DuplicateBarcodeConflict() {
    BookItemUpdateDTO dto = new BookItemUpdateDTO();
    dto.setBarcode("ZYXWVUTSRQPONMLKJIH");

    when(bookItemRepository.findById(bookItemId)).thenReturn(Optional.of(mockBookItem));
    when(bookItemRepository.existsByBarcode(dto.getBarcode())).thenReturn(true);

    assertThrows(
        AppException.class, () -> bookItemService.update(bookItemId, dto, mockUserDetails));
    verify(bookItemRepository, never()).save(any(BookItem.class));
  }

  @Test
  @DisplayName("Update Book Item - Invalid Barcode")
  void testUpdateBookItem_InvalidBarcode() {
    BookItemUpdateDTO dto = new BookItemUpdateDTO();
    dto.setBarcode("ABC123");

    when(bookItemRepository.findById(bookItemId)).thenReturn(Optional.of(mockBookItem));

    assertThrows(
        AppException.class, () -> bookItemService.update(bookItemId, dto, mockUserDetails));
    verify(bookItemRepository, never()).save(any(BookItem.class));
  }

  @Test
  @DisplayName("Update Book Item - Deleted Item Not Found")
  void testUpdateBookItem_DeletedItemNotFound() {
    mockBookItem.setStatus(BookItemStatus.DELETED);
    BookItemUpdateDTO dto = new BookItemUpdateDTO();
    dto.setStatus(BookItemStatus.AVAILABLE);

    when(bookItemRepository.findById(bookItemId)).thenReturn(Optional.of(mockBookItem));

    assertThrows(
        AppException.class, () -> bookItemService.update(bookItemId, dto, mockUserDetails));
    verify(bookItemRepository, never()).save(any(BookItem.class));
  }

  @Test
  @DisplayName("Update Book Item - Cannot Set Status Deleted")
  void testUpdateBookItem_CannotSetStatusDeleted() {
    BookItemUpdateDTO dto = new BookItemUpdateDTO();
    dto.setStatus(BookItemStatus.DELETED);

    when(bookItemRepository.findById(bookItemId)).thenReturn(Optional.of(mockBookItem));

    assertThrows(
        AppException.class, () -> bookItemService.update(bookItemId, dto, mockUserDetails));
    verify(bookItemRepository, never()).save(any(BookItem.class));
  }

  @Test
  @DisplayName("Update Book Item - Borrowed Item Cannot Change Status")
  void testUpdateBookItem_BorrowedItemCannotChangeStatus() {
    mockBookItem.setStatus(BookItemStatus.BORROWED);
    BookItemUpdateDTO dto = new BookItemUpdateDTO();
    dto.setStatus(BookItemStatus.AVAILABLE);

    when(bookItemRepository.findById(bookItemId)).thenReturn(Optional.of(mockBookItem));

    assertThrows(
        AppException.class, () -> bookItemService.update(bookItemId, dto, mockUserDetails));
    verify(bookItemRepository, never()).save(any(BookItem.class));
  }

  @Test
  @DisplayName("Delete Book Item - Success")
  void testDeleteBookItem_Success() {
    when(bookItemRepository.findById(bookItemId)).thenReturn(Optional.of(mockBookItem));

    bookItemService.delete(bookItemId, mockUserDetails);

    assertEquals(BookItemStatus.DELETED, mockBookItem.getStatus());
    verify(bookItemRepository, times(1)).save(mockBookItem);
  }

  @Test
  @DisplayName("Delete Book Item - Borrowed Item Conflict")
  void testDeleteBookItem_BorrowedItemConflict() {
    mockBookItem.setStatus(BookItemStatus.BORROWED);
    when(bookItemRepository.findById(bookItemId)).thenReturn(Optional.of(mockBookItem));

    AppException exception =
        assertThrows(AppException.class, () -> bookItemService.delete(bookItemId, mockUserDetails));

    assertEquals("Ban sach dang duoc muon, khong the xoa", exception.getMessage());
    verify(bookItemRepository, never()).save(any(BookItem.class));
  }

  private Position createPositionTree() {
    Floor floor = new Floor();
    floor.setName("Floor 1");

    Aisle aisle = new Aisle();
    aisle.setName("Aisle A");
    aisle.setFloor(floor);

    Shelf shelf = new Shelf();
    shelf.setId(UUID.randomUUID());
    shelf.setName("Shelf 1");
    shelf.setAisle(aisle);

    Position position = new Position();
    position.setId(UUID.randomUUID());
    position.setShelf(shelf);
    return position;
  }
}
