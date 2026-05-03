package org.app.backend.modules.librarycardrequest;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LibraryCardRequestRepository extends JpaRepository<LibraryCardRequest, UUID> {
  List<LibraryCardRequest> findByUserId(UUID userId);

  List<LibraryCardRequest> findByStatus(LibraryCardRequestStatus status);
}
