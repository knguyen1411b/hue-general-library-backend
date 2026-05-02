package org.app.backend.modules.fine;

import java.util.List;
import java.util.UUID;
import org.app.backend.modules.auth.security.CustomUserDetails;
import org.app.backend.modules.fine.dto.FineResponseDTO;
import org.app.backend.modules.fine.enums.FineStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FineService {
  // Xem nợ
  List<FineResponseDTO> getFinesByUserId(UUID userId, FineStatus status);

  Page<FineResponseDTO> getAllFines(FineStatus status, Pageable pageable);

  // Xử lý thanh toán/hủy
  void processPayment(UUID fineId, CustomUserDetails actor);

  void cancelFine(UUID fineId, String reason, CustomUserDetails actor);
}
