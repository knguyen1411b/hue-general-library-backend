package org.app.backend.modules.payment;

import java.util.UUID;
import org.app.backend.modules.auth.security.CustomUserDetails;
import org.app.backend.modules.payment.dto.PaymentCreateDTO;
import org.app.backend.modules.payment.dto.PaymentDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PaymentService {
  Page<PaymentDTO> findAll(
      Pageable pageable, UUID userId, PaymentType paymentType, PaymentStatus paymentStatus);

  PaymentDTO findById(UUID id);

  PaymentDTO create(PaymentCreateDTO dto, CustomUserDetails actor);

  PaymentDTO confirm(UUID id, CustomUserDetails actor);

  void delete(UUID id, CustomUserDetails actor);
}
