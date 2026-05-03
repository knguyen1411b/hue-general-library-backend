package org.app.backend.modules.fine.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.Data;

@Data
public class FineCreateDTO {
  @NotNull(message = "Rental ID không được để trống")
  UUID rentalId;

  @NotNull(message = "Số tiền không được để trống")
  Double amount;

  @NotNull(message = "Lý do không được để trống")
  String reason;
}
