package org.app.backend.modules.rental;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import org.app.backend.modules.auth.security.CustomUserDetails;
import org.app.backend.modules.book.Book;
import org.app.backend.modules.book.BookRepository;
import org.app.backend.modules.bookItem.BookItem;
import org.app.backend.modules.bookItem.BookItemRepository;
import org.app.backend.modules.bookItem.enums.BookItemStatus;
import org.app.backend.modules.fine.FineRepository;
import org.app.backend.modules.fine.enums.FineStatus;
import org.app.backend.modules.rental.dto.RentalCreateDTO;
import org.app.backend.modules.rental.dto.RentalDTO;
import org.app.backend.modules.rental.enums.RentalStatus;
import org.app.backend.modules.subscription.Subscription;
import org.app.backend.modules.usersubscription.UserSubscription;
import org.app.backend.modules.usersubscription.UserSubscriptionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

@ExtendWith(MockitoExtension.class)
class RentalServiceImplTest {

  @Mock private RentalRepository rentalRepository;
  @Mock private ModelMapper modelMapper;
  @Mock private UserSubscriptionRepository userSubscriptionRepository;
  @Mock private BookItemRepository bookItemRepository;
  @Mock private BookRepository bookRepository;
  @Mock private FineRepository fineRepository;

  @InjectMocks private RentalServiceImpl rentalService;

  private Rental mockRental;
  private CustomUserDetails mockActor;
  private UUID rentalId;
  private UUID userId;
  private UUID bookItemId;

  @BeforeEach
  void setUp() {
    rentalId = UUID.randomUUID();
    userId = UUID.randomUUID();
    bookItemId = UUID.randomUUID();

    mockRental = new Rental();
    mockRental.setId(rentalId);
    mockRental.setUserId(userId);
    mockRental.setBookItemId(bookItemId);
    mockRental.setStatus(RentalStatus.BORROWING);
    mockRental.setDueDate(LocalDate.now().plusDays(7));

    mockActor = new CustomUserDetails();
    mockActor.setId(UUID.randomUUID());
    mockActor.setUsername("admin");
  }

  @Test
  @DisplayName("Find By Id - Success")
  void testFindById_Success() {
    RentalDTO dto = new RentalDTO();
    dto.setId(rentalId);

    when(rentalRepository.findById(rentalId)).thenReturn(Optional.of(mockRental));
    when(modelMapper.map(mockRental, RentalDTO.class)).thenReturn(dto);

    RentalDTO result = rentalService.findById(rentalId);

    assertNotNull(result);
    assertEquals(rentalId, result.getId());
  }

  @Test
  @DisplayName("Create Rental - Fails when no active subscription")
  void testCreate_NoSubscription_ThrowsException() {
    RentalCreateDTO dto = new RentalCreateDTO();
    dto.setUserId(userId);
    dto.setBookItemId(bookItemId);

    when(userSubscriptionRepository.findActiveSubscriptionByUserId(userId))
        .thenReturn(Optional.empty());

    RuntimeException ex =
        assertThrows(RuntimeException.class, () -> rentalService.create(dto, mockActor));
    assertTrue(ex.getMessage().contains("gói cước"));
  }

  @Test
  @DisplayName("Create Rental - Fails when user has unpaid fines")
  void testCreate_UnpaidFine_ThrowsException() {
    RentalCreateDTO dto = new RentalCreateDTO();
    dto.setUserId(userId);
    dto.setBookItemId(bookItemId);

    Subscription subscription = new Subscription();
    subscription.setMaxBooks(5);
    UserSubscription activeSub = new UserSubscription();
    activeSub.setSubscription(subscription);

    when(userSubscriptionRepository.findActiveSubscriptionByUserId(userId))
        .thenReturn(Optional.of(activeSub));
    when(fineRepository.existsByRental_UserIdAndStatus(userId, FineStatus.UNPAID)).thenReturn(true);

    RuntimeException ex =
        assertThrows(RuntimeException.class, () -> rentalService.create(dto, mockActor));
    assertTrue(ex.getMessage().contains("nợ phí phạt"));
  }

  @Test
  @DisplayName("Return Book - On time, sets RETURNED")
  void testReturnBook_OnTime_Success() {
    Book book = new Book();
    book.setId(UUID.randomUUID());
    BookItem bookItem = new BookItem();
    bookItem.setId(bookItemId);
    bookItem.setBook(book);
    bookItem.setStatus(BookItemStatus.BORROWED);

    mockRental.setDueDate(LocalDate.now().plusDays(3)); // not overdue

    RentalDTO resultDto = new RentalDTO();

    when(rentalRepository.findById(rentalId)).thenReturn(Optional.of(mockRental));
    when(bookItemRepository.findById(bookItemId)).thenReturn(Optional.of(bookItem));
    // increaseCount is @Modifying void, no mock needed
    when(rentalRepository.save(mockRental)).thenReturn(mockRental);
    when(modelMapper.map(mockRental, RentalDTO.class)).thenReturn(resultDto);

    rentalService.returnBook(rentalId, mockActor);

    assertEquals(RentalStatus.RETURNED, mockRental.getStatus());
    assertEquals(BookItemStatus.AVAILABLE, bookItem.getStatus());
  }

  @Test
  @DisplayName("Report Lost - Sets rental LOST and book item DISCARDED")
  void testReportLost_Success() {
    Book book = new Book();
    book.setId(UUID.randomUUID());
    book.setTitle("Test Book");
    book.setPrice(100000);

    BookItem bookItem = new BookItem();
    bookItem.setId(bookItemId);
    bookItem.setBook(book);

    RentalDTO resultDto = new RentalDTO();

    when(rentalRepository.findById(rentalId)).thenReturn(Optional.of(mockRental));
    when(bookItemRepository.findById(bookItemId)).thenReturn(Optional.of(bookItem));
    when(userSubscriptionRepository.findActiveSubscriptionByUserId(userId))
        .thenReturn(Optional.empty());
    when(rentalRepository.save(mockRental)).thenReturn(mockRental);
    when(modelMapper.map(mockRental, RentalDTO.class)).thenReturn(resultDto);

    rentalService.reportLost(rentalId, mockActor);

    assertEquals(RentalStatus.LOST, mockRental.getStatus());
    assertEquals(BookItemStatus.DISCARDED, bookItem.getStatus());
  }

  @Test
  @DisplayName("Delete Rental - Success")
  void testDelete_Success() {
    when(rentalRepository.findById(rentalId)).thenReturn(Optional.of(mockRental));

    rentalService.delete(rentalId, mockActor);

    verify(rentalRepository, times(1)).delete(mockRental);
  }
}
