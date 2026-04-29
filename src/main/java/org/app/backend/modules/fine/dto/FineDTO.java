package org.app.backend.modules.fine.dto;

import java.time.Instant;
import java.util.UUID;
import lombok.*;
import org.app.backend.modules.fine.FineStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FineDTO {
  private UUID id;
  private UUID rentalId;
  private Double amount;
  private String reason;
  private FineStatus status;
  private Instant createdAt;
  private Instant updatedAt;
}
