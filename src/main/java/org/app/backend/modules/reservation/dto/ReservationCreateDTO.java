package org.app.backend.modules.reservation.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationCreateDTO {
  @NotNull(message = "User ID is required")
  private UUID userId;

  @NotNull(message = "Book item ID is required")
  private UUID bookItemId;
}
