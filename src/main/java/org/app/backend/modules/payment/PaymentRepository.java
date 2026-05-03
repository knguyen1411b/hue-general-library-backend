package org.app.backend.modules.payment;

import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {
  List<Payment> findByUserId(UUID userId);

  Page<Payment> findByUserId(UUID userId, Pageable pageable);

  List<Payment> findByPaymentStatus(PaymentStatus status);

  Page<Payment> findByPaymentStatus(PaymentStatus status, Pageable pageable);

  List<Payment> findByPaymentType(PaymentType paymentType);

  Page<Payment> findByPaymentType(PaymentType paymentType, Pageable pageable);
}
