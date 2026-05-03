package org.app.backend.modules.fine;

import java.util.UUID;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.app.backend.modules.auth.security.CustomUserDetails;
import org.app.backend.modules.fine.dto.FineCreateDTO;
import org.app.backend.modules.fine.dto.FineDTO;
import org.app.backend.modules.fine.enums.FineStatus;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FineServiceImpl implements FineService {

  FineRepository fineRepository;
  ModelMapper modelMapper;

  @Override
  @Transactional(readOnly = true)
  public Page<FineDTO> findAll(Pageable pageable, UUID rentalId, FineStatus status) {
    if (rentalId != null) {
      return fineRepository
          .findByRentalId(rentalId, pageable)
          .map(f -> modelMapper.map(f, FineDTO.class));
    }
    if (status != null) {
      return fineRepository
          .findByStatus(status, pageable)
          .map(f -> modelMapper.map(f, FineDTO.class));
    }
    return fineRepository.findAll(pageable).map(f -> modelMapper.map(f, FineDTO.class));
  }

  @Override
  @Transactional(readOnly = true)
  public FineDTO findById(UUID id) {
    Fine fine =
        fineRepository.findById(id).orElseThrow(() -> new RuntimeException("Fine not found"));
    return modelMapper.map(fine, FineDTO.class);
  }

  @Override
  @Transactional
  public FineDTO create(FineCreateDTO dto, CustomUserDetails actor) {
    Fine fine = modelMapper.map(dto, Fine.class);
    fine.setStatus(FineStatus.UNPAID);
    Fine saved = fineRepository.save(fine);
    return modelMapper.map(saved, FineDTO.class);
  }

  @Override
  @Transactional
  public FineDTO pay(UUID id, CustomUserDetails actor) {
    Fine fine =
        fineRepository.findById(id).orElseThrow(() -> new RuntimeException("Fine not found"));
    fine.setStatus(FineStatus.PAID);
    Fine saved = fineRepository.save(fine);
    return modelMapper.map(saved, FineDTO.class);
  }

  @Override
  @Transactional
  public void delete(UUID id, CustomUserDetails actor) {
    Fine fine =
        fineRepository.findById(id).orElseThrow(() -> new RuntimeException("Fine not found"));
    fineRepository.delete(fine);
  }
}
