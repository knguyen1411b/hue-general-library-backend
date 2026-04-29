package org.app.backend.modules.fine;

import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FineRepository extends JpaRepository<Fine, UUID> {
  List<Fine> findByRentalId(UUID rentalId);

  Page<Fine> findByRentalId(UUID rentalId, Pageable pageable);

  List<Fine> findByStatus(FineStatus status);

  Page<Fine> findByStatus(FineStatus status, Pageable pageable);
}
