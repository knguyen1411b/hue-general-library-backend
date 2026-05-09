package org.app.backend.modules.rental;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.app.backend.modules.rental.enums.RentalStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RentalRepository extends JpaRepository<Rental, UUID> {
  List<Rental> findByUserId(UUID userId);

  Page<Rental> findByUserId(UUID userId, Pageable pageable);

  List<Rental> findByStatus(RentalStatus status);

  Page<Rental> findByStatus(RentalStatus status, Pageable pageable);

  List<Rental> findByBookItemId(UUID bookItemId);

  Page<Rental> findByBookItemId(UUID bookItemId, Pageable pageable);

  List<Rental> findByStatusAndDueDateBefore(RentalStatus status, LocalDate time);

  @Query("SELECT r FROM Rental r WHERE r.status = org.app.backend.modules.rental.enums.RentalStatus.BORROWING AND r.dueDate BETWEEN :startDate AND :endDate")
  List<Rental> findBorrowingRentalsWithDueDateBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
