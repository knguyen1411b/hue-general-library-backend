package org.app.backend.modules.librarycard;

import java.util.UUID;
import org.app.backend.modules.auth.security.CustomUserDetails;
import org.app.backend.modules.librarycard.dto.LibraryCardCreateDTO;
import org.app.backend.modules.librarycard.dto.LibraryCardDTO;
import org.app.backend.modules.librarycard.dto.LibraryCardUpdateDTO;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LibraryCardServiceImpl implements LibraryCardService {

  private final LibraryCardRepository libraryCardRepository;
  private final ModelMapper modelMapper;
  private final org.app.backend.modules.librarycardrequest.LibraryCardRequestRepository
      requestRepository;
  private final org.app.backend.modules.user.UserRepository userRepository;

  public LibraryCardServiceImpl(
      LibraryCardRepository libraryCardRepository,
      ModelMapper modelMapper,
      org.app.backend.modules.librarycardrequest.LibraryCardRequestRepository requestRepository,
      org.app.backend.modules.user.UserRepository userRepository) {
    this.libraryCardRepository = libraryCardRepository;
    this.modelMapper = modelMapper;
    this.requestRepository = requestRepository;
    this.userRepository = userRepository;
  }

  @Override
  public Page<LibraryCardDTO> findAll(Pageable pageable, UUID userId, CardStatus status) {
    if (userId != null) {
      return libraryCardRepository
          .findByUserId(userId, pageable)
          .map(card -> modelMapper.map(card, LibraryCardDTO.class));
    }
    if (status != null) {
      return libraryCardRepository
          .findByStatus(status, pageable)
          .map(card -> modelMapper.map(card, LibraryCardDTO.class));
    }
    return libraryCardRepository
        .findAll(pageable)
        .map(card -> modelMapper.map(card, LibraryCardDTO.class));
  }

  @Override
  public LibraryCardDTO findById(UUID id) {
    LibraryCard card =
        libraryCardRepository
            .findById(id)
            .orElseThrow(() -> new RuntimeException("Library card not found"));
    return modelMapper.map(card, LibraryCardDTO.class);
  }

  @Override
  public LibraryCardDTO create(LibraryCardCreateDTO dto, CustomUserDetails actor) {
    LibraryCard card = modelMapper.map(dto, LibraryCard.class);
    card.setStatus(dto.getStatus() != null ? dto.getStatus() : CardStatus.ACTIVE);
    LibraryCard saved = libraryCardRepository.save(card);
    return modelMapper.map(saved, LibraryCardDTO.class);
  }

  @Override
  public LibraryCardDTO update(UUID id, LibraryCardUpdateDTO dto, CustomUserDetails actor) {
    LibraryCard card =
        libraryCardRepository
            .findById(id)
            .orElseThrow(() -> new RuntimeException("Library card not found"));
    if (dto.getIssueDate() != null) card.setIssueDate(dto.getIssueDate());
    if (dto.getExpiryDate() != null) card.setExpiryDate(dto.getExpiryDate());
    if (dto.getStatus() != null) card.setStatus(dto.getStatus());
    LibraryCard saved = libraryCardRepository.save(card);
    return modelMapper.map(saved, LibraryCardDTO.class);
  }

  @Override
  public void delete(UUID id, CustomUserDetails actor) {
    LibraryCard card =
        libraryCardRepository
            .findById(id)
            .orElseThrow(() -> new RuntimeException("Library card not found"));
    libraryCardRepository.delete(card);
  }

  @Override
  public LibraryCardDTO lock(UUID id, CustomUserDetails actor) {
    LibraryCard card =
        libraryCardRepository
            .findById(id)
            .orElseThrow(() -> new RuntimeException("Library card not found"));
    card.setStatus(CardStatus.BLOCKED);
    LibraryCard saved = libraryCardRepository.save(card);
    return modelMapper.map(saved, LibraryCardDTO.class);
  }

  @Override
  public void requestPhysicalCard(
      CustomUserDetails user,
      org.app.backend.modules.librarycardrequest.dto.LibraryCardRequestCreateDTO dto) {
    org.app.backend.modules.user.User u =
        userRepository
            .findById(user.getId())
            .orElseThrow(() -> new RuntimeException("User not found"));
    org.app.backend.modules.librarycardrequest.LibraryCardRequest request =
        org.app.backend.modules.librarycardrequest.LibraryCardRequest.builder()
            .user(u)
            .status(org.app.backend.modules.librarycardrequest.LibraryCardRequestStatus.PENDING)
            .deliveryAddress(dto.getDeliveryAddress())
            .note(dto.getNote())
            .build();
    requestRepository.save(request);
  }

  @Override
  public LibraryCardDTO replace(UUID id, CustomUserDetails actor) {
    LibraryCard oldCard =
        libraryCardRepository
            .findById(id)
            .orElseThrow(() -> new RuntimeException("Library card not found"));
    // Create new card with same user
    LibraryCard newCard = new LibraryCard();
    newCard.setUserId(oldCard.getUserId());
    newCard.setIssueDate(java.time.LocalDate.now());
    newCard.setExpiryDate(java.time.LocalDate.now().plusYears(1));
    newCard.setStatus(CardStatus.ACTIVE);
    // Invalidate old card
    oldCard.setStatus(CardStatus.INACTIVE);
    libraryCardRepository.save(oldCard);
    LibraryCard saved = libraryCardRepository.save(newCard);
    return modelMapper.map(saved, LibraryCardDTO.class);
  }
}
