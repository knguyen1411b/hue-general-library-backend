package org.app.backend.modules.fine;

import org.app.backend.modules.fine.dto.FineDTO;
import org.app.backend.modules.fine.dto.FineCreateDTO;
import org.app.backend.modules.auth.security.CustomUserDetails;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
@Transactional
public class FineServiceImpl implements FineService {

  private final FineRepository fineRepository;
  private final ModelMapper modelMapper;

  public FineServiceImpl(FineRepository fineRepository, ModelMapper modelMapper) {
    this.fineRepository = fineRepository;
    this.modelMapper = modelMapper;
  }

  @Override
  public Page<FineDTO> findAll(Pageable pageable, UUID rentalId, FineStatus status) {
    if (rentalId != null) {
      return fineRepository.findByRentalId(rentalId, pageable).map(f -> modelMapper.map(f, FineDTO.class));
    }
    if (status != null) {
      return fineRepository.findByStatus(status, pageable).map(f -> modelMapper.map(f, FineDTO.class));
    }
    return fineRepository.findAll(pageable).map(f -> modelMapper.map(f, FineDTO.class));
  }

  @Override
  public FineDTO findById(UUID id) {
    Fine fine = fineRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Fine not found"));
    return modelMapper.map(fine, FineDTO.class);
  }

  @Override
  public FineDTO create(FineCreateDTO dto, CustomUserDetails actor) {
    Fine fine = modelMapper.map(dto, Fine.class);
    fine.setStatus(FineStatus.UNPAID);
    Fine saved = fineRepository.save(fine);
    return modelMapper.map(saved, FineDTO.class);
  }

  @Override
  public FineDTO pay(UUID id, CustomUserDetails actor) {
    Fine fine = fineRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Fine not found"));
    fine.setStatus(FineStatus.PAID);
    Fine saved = fineRepository.save(fine);
    return modelMapper.map(saved, FineDTO.class);
  }

  @Override
  public void delete(UUID id, CustomUserDetails actor) {
    Fine fine = fineRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Fine not found"));
    fineRepository.delete(fine);
  }
}
