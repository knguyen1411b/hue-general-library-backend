package org.app.backend.modules.payment.dto;

import org.app.backend.modules.payment.PaymentType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.UUID;

@Data
public class PaymentCreateDTO {
  @NotNull(message = "User ID không được để trống")
  UUID userId;

  @NotNull(message = "Số tiền không được để trống")
  Double amount;

  @NotNull(message = "Loại thanh toán không được để trống")
  PaymentType paymentType;

  UUID fineId;
  UUID userSubscriptionId;
}
