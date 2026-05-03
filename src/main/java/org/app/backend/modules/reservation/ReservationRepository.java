package org.app.backend.modules.reservation;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, UUID> {
  List<Reservation> findByUserIdAndStatus(UUID userId, ReservationStatus status);

  List<Reservation> findByBookItemIdAndStatus(UUID bookItemId, ReservationStatus status);
}
