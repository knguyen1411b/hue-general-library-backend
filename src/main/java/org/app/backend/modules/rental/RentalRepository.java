package org.app.backend.modules.rental;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
import java.util.List;

public interface RentalRepository extends JpaRepository<Rental, UUID> {
  List<Rental> findByUserId(UUID userId);
  Page<Rental> findByUserId(UUID userId, Pageable pageable);
  List<Rental> findByStatus(RentalStatus status);
  Page<Rental> findByStatus(RentalStatus status, Pageable pageable);
  List<Rental> findByBookItemId(UUID bookItemId);
  Page<Rental> findByBookItemId(UUID bookItemId, Pageable pageable);
}
