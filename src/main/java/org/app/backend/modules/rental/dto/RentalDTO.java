package org.app.backend.modules.rental.dto;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;
import lombok.*;
import org.app.backend.modules.rental.enums.RentalStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RentalDTO {
  private UUID id;
  private UUID userId;
  private UUID bookItemId;
  private LocalDate rentDate;
  private LocalDate dueDate;
  private LocalDate returnDate;
  private RentalStatus status;
  private Instant createdAt;
  private Instant updatedAt;
}
