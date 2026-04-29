package org.app.backend.modules.librarycard;

import org.app.backend.modules.librarycard.dto.LibraryCardDTO;
import org.app.backend.modules.librarycard.dto.LibraryCardCreateDTO;
import org.app.backend.modules.librarycard.dto.LibraryCardUpdateDTO;
import org.app.backend.modules.auth.security.CustomUserDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface LibraryCardService {
  Page<LibraryCardDTO> findAll(Pageable pageable, UUID userId, CardStatus status);
  LibraryCardDTO findById(UUID id);
  LibraryCardDTO create(LibraryCardCreateDTO dto, CustomUserDetails actor);
  LibraryCardDTO update(UUID id, LibraryCardUpdateDTO dto, CustomUserDetails actor);
  void delete(UUID id, CustomUserDetails actor);
  LibraryCardDTO lock(UUID id, CustomUserDetails actor);
  LibraryCardDTO replace(UUID id, CustomUserDetails actor);
}
