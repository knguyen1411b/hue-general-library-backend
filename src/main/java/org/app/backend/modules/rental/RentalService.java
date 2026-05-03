package org.app.backend.modules.rental;

import java.util.UUID;
import org.app.backend.modules.auth.security.CustomUserDetails;
import org.app.backend.modules.fine.enums.FineStatus;
import org.app.backend.modules.rental.dto.RentalCreateDTO;
import org.app.backend.modules.rental.dto.RentalDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.app.backend.modules.rental.enums.RentalStatus;


public interface RentalService {
  Page<RentalDTO> findAll(Pageable pageable, UUID userId, RentalStatus status, UUID bookItemId);

  RentalDTO findById(UUID id);

  RentalDTO create(RentalCreateDTO dto, CustomUserDetails actor);

  RentalDTO returnBook(UUID id, CustomUserDetails actor);

  RentalDTO renewBook(UUID id, CustomUserDetails actor);

  RentalDTO reportLost(UUID id, CustomUserDetails actor);

  void delete(UUID id, CustomUserDetails actor);
}
