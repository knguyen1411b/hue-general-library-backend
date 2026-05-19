package org.app.backend.modules.librarycardrequest;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LibraryCardRequestRepository extends JpaRepository<LibraryCardRequest, UUID> {

  @EntityGraph(attributePaths = {"user"})
  Page<LibraryCardRequest> findByUserId(UUID userId, Pageable pageable);

  @EntityGraph(attributePaths = {"user"})
  Page<LibraryCardRequest> findByStatus(LibraryCardRequestStatus status, Pageable pageable);

  @EntityGraph(attributePaths = {"user"})
  Page<LibraryCardRequest> findAll(Pageable pageable);

  @EntityGraph(attributePaths = {"user"})
  Optional<LibraryCardRequest> findWithUserById(UUID id);

  boolean existsByUserIdAndStatus(UUID userId, LibraryCardRequestStatus status);
}
