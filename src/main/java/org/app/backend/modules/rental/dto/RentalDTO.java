package org.app.backend.modules.rental.dto;

import org.app.backend.modules.rental.RentalStatus;
import lombok.*;
import java.time.LocalDate;
import java.time.Instant;
import java.util.UUID;

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
