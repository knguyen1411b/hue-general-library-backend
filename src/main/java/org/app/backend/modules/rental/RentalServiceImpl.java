package org.app.backend.modules.rental;

import org.app.backend.modules.rental.dto.RentalDTO;
import org.app.backend.modules.rental.dto.RentalCreateDTO;
import org.app.backend.modules.rental.dto.RentalUpdateDTO;
import org.app.backend.modules.auth.security.CustomUserDetails;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.UUID;

@Service
@Transactional
public class RentalServiceImpl implements RentalService {

  private final RentalRepository rentalRepository;
  private final ModelMapper modelMapper;

  public RentalServiceImpl(RentalRepository rentalRepository, ModelMapper modelMapper) {
    this.rentalRepository = rentalRepository;
    this.modelMapper = modelMapper;
  }

  @Override
  public Page<RentalDTO> findAll(Pageable pageable, UUID userId, RentalStatus status, UUID bookItemId) {
    // Simple implementation - can be enhanced with Specification for complex filtering
    if (userId != null) {
      return rentalRepository.findByUserId(userId, pageable).map(r -> modelMapper.map(r, RentalDTO.class));
    }
    if (status != null) {
      return rentalRepository.findByStatus(status, pageable).map(r -> modelMapper.map(r, RentalDTO.class));
    }
    if (bookItemId != null) {
      return rentalRepository.findByBookItemId(bookItemId, pageable).map(r -> modelMapper.map(r, RentalDTO.class));
    }
    return rentalRepository.findAll(pageable).map(r -> modelMapper.map(r, RentalDTO.class));
  }

  @Override
  public RentalDTO findById(UUID id) {
    Rental rental = rentalRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Rental not found"));
    return modelMapper.map(rental, RentalDTO.class);
  }

  @Override
  public RentalDTO create(RentalCreateDTO dto, CustomUserDetails actor) {
    Rental rental = modelMapper.map(dto, Rental.class);
    rental.setStatus(RentalStatus.BORROWING);
    Rental saved = rentalRepository.save(rental);
    return modelMapper.map(saved, RentalDTO.class);
  }

  @Override
  public RentalDTO returnBook(UUID id, CustomUserDetails actor) {
    Rental rental = rentalRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Rental not found"));
    rental.setReturnDate(java.time.LocalDate.now());
    rental.setStatus(RentalStatus.RETURNED);
    Rental saved = rentalRepository.save(rental);
    return modelMapper.map(saved, RentalDTO.class);
  }

  @Override
  public RentalDTO renewBook(UUID id, CustomUserDetails actor) {
    Rental rental = rentalRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Rental not found"));
    // Add renewal logic here - e.g., extend due date
    Rental saved = rentalRepository.save(rental);
    return modelMapper.map(saved, RentalDTO.class);
  }

  @Override
  public RentalDTO reportLost(UUID id, CustomUserDetails actor) {
    Rental rental = rentalRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Rental not found"));
    rental.setStatus(RentalStatus.LOST);
    Rental saved = rentalRepository.save(rental);
    return modelMapper.map(saved, RentalDTO.class);
  }

  @Override
  public void delete(UUID id, CustomUserDetails actor) {
    Rental rental = rentalRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Rental not found"));
    rentalRepository.delete(rental);
  }
}
