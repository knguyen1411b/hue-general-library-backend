package org.app.backend.modules.librarycard.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.UUID;
import lombok.Data;
import org.app.backend.modules.librarycard.CardStatus;

@Data
public class LibraryCardCreateDTO {
  @NotNull(message = "User ID không được để trống")
  UUID userId;

  @NotNull(message = "Ngày cấp không được để trống")
  LocalDate issueDate;

  @NotNull(message = "Ngày hết hạn không được để trống")
  LocalDate expiryDate;

  CardStatus status;
}
