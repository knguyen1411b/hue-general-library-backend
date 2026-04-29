package org.app.backend.modules.librarycard.dto;

import org.app.backend.modules.librarycard.CardStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;
import java.util.UUID;

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
