package org.app.backend.modules.librarycard;

import java.util.UUID;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.app.backend.common.exception.AppException;
import org.app.backend.modules.auth.security.CustomUserDetails;
import org.app.backend.modules.librarycard.dto.LibraryCardCreateDTO;
import org.app.backend.modules.librarycard.dto.LibraryCardDTO;
import org.app.backend.modules.librarycard.dto.LibraryCardUpdateDTO;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LibraryCardServiceImpl implements LibraryCardService {

  LibraryCardRepository libraryCardRepository;
  ModelMapper modelMapper;
  org.app.backend.modules.librarycardrequest.LibraryCardRequestRepository requestRepository;
  org.app.backend.modules.user.UserRepository userRepository;

  @Override
  @Transactional(readOnly = true)
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
  @Transactional(readOnly = true)
  public LibraryCardDTO findById(UUID id) {
    LibraryCard card =
        libraryCardRepository
            .findById(id)
            .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Không tìm thấy thẻ thư viện"));
    return modelMapper.map(card, LibraryCardDTO.class);
  }

  @Override
  @Transactional
  public LibraryCardDTO create(LibraryCardCreateDTO dto, CustomUserDetails actor) {
    LibraryCard card = new LibraryCard();
    card.setUserId(dto.getUserId());
    card.setIssueDate(dto.getIssueDate());
    card.setExpiryDate(dto.getExpiryDate());
    card.setStatus(dto.getStatus() != null ? dto.getStatus() : CardStatus.ACTIVE);
    LibraryCard saved = libraryCardRepository.save(card);
    return modelMapper.map(saved, LibraryCardDTO.class);
  }

  @Override
  @Transactional
  public LibraryCardDTO update(UUID id, LibraryCardUpdateDTO dto, CustomUserDetails actor) {
    LibraryCard card =
        libraryCardRepository
            .findById(id)
            .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Không tìm thấy thẻ thư viện"));
    if (dto.getIssueDate() != null) card.setIssueDate(dto.getIssueDate());
    if (dto.getExpiryDate() != null) card.setExpiryDate(dto.getExpiryDate());
    if (dto.getStatus() != null) card.setStatus(dto.getStatus());
    LibraryCard saved = libraryCardRepository.save(card);
    return modelMapper.map(saved, LibraryCardDTO.class);
  }

  @Override
  @Transactional
  public void delete(UUID id, CustomUserDetails actor) {
    LibraryCard card =
        libraryCardRepository
            .findById(id)
            .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Không tìm thấy thẻ thư viện"));
    libraryCardRepository.delete(card);
  }

  @Override
  @Transactional
  public LibraryCardDTO lock(UUID id, CustomUserDetails actor) {
    LibraryCard card =
        libraryCardRepository
            .findById(id)
            .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Không tìm thấy thẻ thư viện"));
    card.setStatus(CardStatus.BLOCKED);
    LibraryCard saved = libraryCardRepository.save(card);
    return modelMapper.map(saved, LibraryCardDTO.class);
  }

  @Override
  @Transactional
  public void requestPhysicalCard(
      CustomUserDetails user,
      org.app.backend.modules.librarycardrequest.dto.LibraryCardRequestCreateDTO dto) {
    org.app.backend.modules.user.User u =
        userRepository
            .findById(user.getId())
            .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Không tìm thấy người dùng"));

    if (requestRepository.existsByUserIdAndStatus(
            user.getId(),
            org.app.backend.modules.librarycardrequest.LibraryCardRequestStatus.PENDING)) {
      throw new AppException(
          HttpStatus.BAD_REQUEST,
          "Bạn đã có yêu cầu cấp thẻ đang chờ xử lý. Vui lòng chờ thủ thư phê duyệt.");
    }

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
  @Transactional
  public LibraryCardDTO replace(UUID id, CustomUserDetails actor) {
    LibraryCard oldCard =
        libraryCardRepository
            .findById(id)
            .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Không tìm thấy thẻ thư viện"));
    LibraryCard newCard = new LibraryCard();
    newCard.setUserId(oldCard.getUserId());
    newCard.setIssueDate(java.time.LocalDate.now());
    newCard.setExpiryDate(java.time.LocalDate.now().plusYears(1));
    newCard.setStatus(CardStatus.ACTIVE);
    oldCard.setStatus(CardStatus.INACTIVE);
    libraryCardRepository.save(oldCard);
    LibraryCard saved = libraryCardRepository.save(newCard);
    return modelMapper.map(saved, LibraryCardDTO.class);
  }
}
