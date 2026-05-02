package org.app.backend.modules.rental.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;
import lombok.Data;

@Data
public class RentalCreateDTO {
  @NotNull(message = "ID Độc giả không được để trống")
  UUID userId;

  @NotEmpty(message = "Danh sách mã vạch (barcode) không được để trống")
  List<String> barcodes;
}
