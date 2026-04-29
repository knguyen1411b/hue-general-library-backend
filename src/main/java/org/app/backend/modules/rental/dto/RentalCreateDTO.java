package org.app.backend.modules.rental.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.UUID;
import lombok.Data;

@Data
public class RentalCreateDTO {
  @NotNull(message = "User ID không được để trống")
  UUID userId;

  @NotNull(message = "Book item ID không được để trống")
  UUID bookItemId;

  @NotNull(message = "Ngày mượn không được để trống")
  LocalDate rentDate;

  @NotNull(message = "Ngày đến hạn không được để trống")
  LocalDate dueDate;
}
