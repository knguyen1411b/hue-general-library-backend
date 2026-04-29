package org.app.backend.modules.payment.dto;

import org.app.backend.modules.payment.PaymentStatus;
import org.app.backend.modules.payment.PaymentType;
import lombok.Data;
import java.util.UUID;

@Data
public class PaymentUpdateDTO {
  Double amount;
  PaymentStatus paymentStatus;
  PaymentType paymentType;
  UUID fineId;
  UUID userSubscriptionId;
}
