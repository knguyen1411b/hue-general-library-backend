package org.app.backend.modules.librarycardrequest;

import java.util.UUID;
import org.app.backend.modules.librarycardrequest.dto.LibraryCardRequestDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LibraryCardRequestService {
  LibraryCardRequest createRequest(LibraryCardRequest request);

  Page<LibraryCardRequestDTO> getAllRequests(Pageable pageable);

  Page<LibraryCardRequestDTO> getAllRequests(
      Pageable pageable, UUID userId, LibraryCardRequestStatus status);

  LibraryCardRequestDTO getRequestById(UUID id);

  Page<LibraryCardRequestDTO> getRequestsByUserId(UUID userId, Pageable pageable);

  boolean hasPendingRequestByUserId(UUID userId);

  LibraryCardRequestDTO updateRequestStatus(UUID id, LibraryCardRequestStatus status, String note);

  LibraryCardRequestDTO cancelRequest(UUID id, UUID userId);
}
