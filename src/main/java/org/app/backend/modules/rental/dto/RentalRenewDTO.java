package org.app.backend.modules.rental.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RentalRenewDTO {
  @NotBlank(message = "Mã vạch sách không được để trống")
  String barcode;
}
