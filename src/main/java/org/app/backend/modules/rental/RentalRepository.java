package org.app.backend.modules.rental;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.app.backend.modules.rental.enums.RentalStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RentalRepository extends JpaRepository<Rental, UUID> {

  // Đếm số lượng sách user ĐANG MƯỢN (phục vụ luồng check giới hạn gói cước)
  int countByUser_IdAndStatus(UUID userId, RentalStatus status);

  // Tìm giao dịch đang mượn dựa vào mã vạch của cuốn sách (phục vụ luồng trả sách)
  Optional<Rental> findByBookItem_BarcodeAndStatus(String barcode, RentalStatus status);

  // Độc giả: Xem lịch sử mượn của chính mình
  Page<Rental> findByUser_IdOrderByDueDateDesc(UUID userId, Pageable pageable);

  // Thủ thư: Xem toàn bộ phiếu mượn (có thể kết hợp filter sau này)
  Page<Rental> findAll(Pageable pageable);

  // Cron Job: Tìm các phiếu mượn ĐANG MƯỢN nhưng HẠN TRẢ < HIỆN TẠI
  List<Rental> findByStatusAndDueDateBefore(RentalStatus status, Instant time);
}
