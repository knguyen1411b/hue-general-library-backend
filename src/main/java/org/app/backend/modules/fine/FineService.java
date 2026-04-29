package org.app.backend.modules.fine;

import org.app.backend.modules.fine.dto.FineDTO;
import org.app.backend.modules.fine.dto.FineCreateDTO;
import org.app.backend.modules.fine.dto.FineUpdateDTO;
import org.app.backend.modules.auth.security.CustomUserDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.UUID;

public interface FineService {
  Page<FineDTO> findAll(Pageable pageable, UUID rentalId, FineStatus status);
  FineDTO findById(UUID id);
  FineDTO create(FineCreateDTO dto, CustomUserDetails actor);
  FineDTO pay(UUID id, CustomUserDetails actor);
  void delete(UUID id, CustomUserDetails actor);
}
