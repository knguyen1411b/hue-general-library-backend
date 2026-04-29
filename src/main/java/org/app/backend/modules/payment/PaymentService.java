package org.app.backend.modules.payment;

import org.app.backend.modules.payment.dto.PaymentDTO;
import org.app.backend.modules.payment.dto.PaymentCreateDTO;
import org.app.backend.modules.payment.dto.PaymentUpdateDTO;
import org.app.backend.modules.auth.security.CustomUserDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.UUID;

public interface PaymentService {
  Page<PaymentDTO> findAll(Pageable pageable, UUID userId, PaymentType paymentType, PaymentStatus paymentStatus);
  PaymentDTO findById(UUID id);
  PaymentDTO create(PaymentCreateDTO dto, CustomUserDetails actor);
  PaymentDTO confirm(UUID id, CustomUserDetails actor);
  void delete(UUID id, CustomUserDetails actor);
}
