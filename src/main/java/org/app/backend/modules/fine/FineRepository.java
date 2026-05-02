package org.app.backend.modules.fine;

import java.util.List;
import java.util.UUID;
import org.app.backend.modules.fine.enums.FineStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FineRepository extends JpaRepository<Fine, UUID> {

  // 1. Phục vụ luồng Rental (Mượn/Gia hạn sách): Kiểm tra xem user có đang nợ tiền không
  boolean existsByRental_User_IdAndStatus(UUID userId, FineStatus status);

  // 2. Phục vụ luồng Fine (Dành cho độc giả): Xem danh sách các khoản nợ của chính mình
  List<Fine> findByRental_User_IdAndStatusOrderByAmountDesc(UUID userId, FineStatus status);

  // 3. Phục vụ luồng Fine (Dành cho thủ thư): Lấy danh sách tất cả các phiếu phạt trên hệ thống
  Page<Fine> findByStatus(FineStatus status, Pageable pageable);
}
