package org.app.backend.modules.fine;

import java.util.UUID;
import org.app.backend.modules.auth.security.CustomUserDetails;
import org.app.backend.modules.fine.dto.FineCreateDTO;
import org.app.backend.modules.fine.dto.FineDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.app.backend.modules.fine.enums.FineStatus;


public interface FineService {
  Page<FineDTO> findAll(Pageable pageable, UUID rentalId, FineStatus status);

  FineDTO findById(UUID id);

  FineDTO create(FineCreateDTO dto, CustomUserDetails actor);

  FineDTO pay(UUID id, CustomUserDetails actor);

  void delete(UUID id, CustomUserDetails actor);
}
