package org.app.backend.modules.payment.dto;

import org.app.backend.modules.payment.PaymentStatus;
import org.app.backend.modules.payment.PaymentType;
import lombok.*;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDTO {
  private UUID id;
  private UUID userId;
  private Double amount;
  private PaymentStatus paymentStatus;
  private PaymentType paymentType;
  private UUID fineId;
  private UUID userSubscriptionId;
  private Instant createdAt;
  private Instant updatedAt;
}
