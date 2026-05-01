package org.app.backend.modules.rental;

import java.util.UUID;
import org.app.backend.modules.auth.security.CustomUserDetails;
import org.app.backend.modules.rental.dto.RentalCreateDTO;
import org.app.backend.modules.rental.dto.RentalPreviewDTO;
import org.app.backend.modules.rental.dto.RentalRenewDTO;
import org.app.backend.modules.rental.dto.RentalResponseDTO;
import org.app.backend.modules.rental.dto.RentalReturnDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RentalService {
  void rentBooks(RentalCreateDTO dto, CustomUserDetails currentUser);

  RentalPreviewDTO getReturnPreview(String barcode);

  void returnBooks(RentalReturnDTO dto, CustomUserDetails librarian);

  void renewBook(RentalRenewDTO dto, CustomUserDetails librarian);

  Page<RentalResponseDTO> getMyRentals(UUID userId, Pageable pageable);

  Page<RentalResponseDTO> getAllRentals(Pageable pageable);
}
