package org.app.backend.modules.payment.dto;

import java.util.UUID;
import lombok.Data;
import org.app.backend.modules.payment.PaymentStatus;
import org.app.backend.modules.payment.PaymentType;

@Data
public class PaymentUpdateDTO {
  Double amount;
  PaymentStatus paymentStatus;
  PaymentType paymentType;
  UUID fineId;
  UUID userSubscriptionId;
}
