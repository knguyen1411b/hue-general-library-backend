package org.app.backend.modules.fine;

import java.util.List;
import java.util.UUID;
import org.app.backend.modules.fine.enums.FineStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface FineRepository extends JpaRepository<Fine, UUID> {

  List<Fine> findByRentalId(UUID rentalId);

  Page<Fine> findByRentalId(UUID rentalId, Pageable pageable);

  List<Fine> findByStatus(FineStatus status);

  Page<Fine> findByStatus(FineStatus status, Pageable pageable);

  // tạo hàm existsByRental_User_IdAndStatus để kiểm tra xem có tồn tại phạt nào chưa thanh toán của
  // người dùng hay không
  boolean existsByRental_UserIdAndStatus(UUID userId, FineStatus status);

  boolean existsByRental_Id(UUID rentalId);
  
  Optional<Fine> findByRental_Id(UUID rentalId);
}
