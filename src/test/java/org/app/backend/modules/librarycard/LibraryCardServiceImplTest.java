package org.app.backend.modules.librarycard;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.UUID;
import org.app.backend.modules.auth.security.CustomUserDetails;
import org.app.backend.modules.librarycard.dto.LibraryCardCreateDTO;
import org.app.backend.modules.librarycard.dto.LibraryCardDTO;
import org.app.backend.modules.librarycardrequest.LibraryCardRequestRepository;
import org.app.backend.modules.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

@ExtendWith(MockitoExtension.class)
class LibraryCardServiceImplTest {

  @Mock private LibraryCardRepository libraryCardRepository;
  @Mock private ModelMapper modelMapper;
  @Mock private LibraryCardRequestRepository requestRepository;
  @Mock private UserRepository userRepository;

  @InjectMocks private LibraryCardServiceImpl libraryCardService;

  private LibraryCard mockCard;
  private CustomUserDetails mockActor;
  private UUID cardId;

  @BeforeEach
  void setUp() {
    cardId = UUID.randomUUID();
    mockCard = new LibraryCard();
    mockCard.setId(cardId);
    mockCard.setStatus(CardStatus.ACTIVE);

    mockActor = new CustomUserDetails();
    mockActor.setId(UUID.randomUUID());
    mockActor.setUsername("admin");
  }

  @Test
  @DisplayName("Find By Id - Success")
  void testFindById_Success() {
    LibraryCardDTO mockDto = new LibraryCardDTO();
    mockDto.setId(cardId);

    when(libraryCardRepository.findById(cardId)).thenReturn(Optional.of(mockCard));
    when(modelMapper.map(mockCard, LibraryCardDTO.class)).thenReturn(mockDto);

    LibraryCardDTO result = libraryCardService.findById(cardId);

    assertNotNull(result);
    assertEquals(cardId, result.getId());
  }

  @Test
  @DisplayName("Find By Id - Not Found")
  void testFindById_NotFound() {
    when(libraryCardRepository.findById(cardId)).thenReturn(Optional.empty());

    RuntimeException ex =
        assertThrows(RuntimeException.class, () -> libraryCardService.findById(cardId));
    assertEquals("Library card not found", ex.getMessage());
  }

  @Test
  @DisplayName("Create Library Card - Success")
  void testCreate_Success() {
    LibraryCardCreateDTO dto = new LibraryCardCreateDTO();
    dto.setStatus(CardStatus.ACTIVE);

    LibraryCard mappedCard = new LibraryCard();
    mappedCard.setId(UUID.randomUUID());
    LibraryCardDTO resultDto = new LibraryCardDTO();

    when(modelMapper.map(dto, LibraryCard.class)).thenReturn(mappedCard);
    when(libraryCardRepository.save(mappedCard)).thenReturn(mappedCard);
    when(modelMapper.map(mappedCard, LibraryCardDTO.class)).thenReturn(resultDto);

    LibraryCardDTO result = libraryCardService.create(dto, mockActor);

    assertNotNull(result);
    verify(libraryCardRepository, times(1)).save(mappedCard);
    assertEquals(CardStatus.ACTIVE, mappedCard.getStatus());
  }

  @Test
  @DisplayName("Lock Library Card - Blocks card")
  void testLock_Success() {
    LibraryCardDTO blockedDto = new LibraryCardDTO();
    when(libraryCardRepository.findById(cardId)).thenReturn(Optional.of(mockCard));
    when(libraryCardRepository.save(mockCard)).thenReturn(mockCard);
    when(modelMapper.map(mockCard, LibraryCardDTO.class)).thenReturn(blockedDto);

    libraryCardService.lock(cardId, mockActor);

    assertEquals(CardStatus.BLOCKED, mockCard.getStatus());
    verify(libraryCardRepository, times(1)).save(mockCard);
  }

  @Test
  @DisplayName("Replace Library Card - Creates new card and deactivates old")
  void testReplace_Success() {
    LibraryCardDTO newCardDto = new LibraryCardDTO();
    LibraryCard savedNewCard = new LibraryCard();
    savedNewCard.setId(UUID.randomUUID());

    when(libraryCardRepository.findById(cardId)).thenReturn(Optional.of(mockCard));
    when(libraryCardRepository.save(any(LibraryCard.class))).thenReturn(savedNewCard);
    when(modelMapper.map(savedNewCard, LibraryCardDTO.class)).thenReturn(newCardDto);

    libraryCardService.replace(cardId, mockActor);

    assertEquals(CardStatus.INACTIVE, mockCard.getStatus());
    // save is called twice: once for old card (inactive), once for new card
    verify(libraryCardRepository, times(2)).save(any(LibraryCard.class));
  }

  @Test
  @DisplayName("Delete Library Card - Success")
  void testDelete_Success() {
    when(libraryCardRepository.findById(cardId)).thenReturn(Optional.of(mockCard));

    libraryCardService.delete(cardId, mockActor);

    verify(libraryCardRepository, times(1)).delete(mockCard);
  }
}
