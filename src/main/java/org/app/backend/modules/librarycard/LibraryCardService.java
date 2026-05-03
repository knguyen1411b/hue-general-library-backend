package org.app.backend.modules.librarycard;

import java.util.UUID;
import org.app.backend.modules.auth.security.CustomUserDetails;
import org.app.backend.modules.librarycard.dto.LibraryCardCreateDTO;
import org.app.backend.modules.librarycard.dto.LibraryCardDTO;
import org.app.backend.modules.librarycard.dto.LibraryCardUpdateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LibraryCardService {
  Page<LibraryCardDTO> findAll(Pageable pageable, UUID userId, CardStatus status);

  LibraryCardDTO findById(UUID id);

  LibraryCardDTO create(LibraryCardCreateDTO dto, CustomUserDetails actor);

  LibraryCardDTO update(UUID id, LibraryCardUpdateDTO dto, CustomUserDetails actor);

  void delete(UUID id, CustomUserDetails actor);

  LibraryCardDTO lock(UUID id, CustomUserDetails actor);

  LibraryCardDTO replace(UUID id, CustomUserDetails actor);

  void requestPhysicalCard(
      CustomUserDetails user,
      org.app.backend.modules.librarycardrequest.dto.LibraryCardRequestCreateDTO dto);
}
