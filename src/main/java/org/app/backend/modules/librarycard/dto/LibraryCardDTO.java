package org.app.backend.modules.librarycard.dto;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;
import lombok.*;
import org.app.backend.modules.librarycard.CardStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LibraryCardDTO {
  private UUID id;
  private UUID userId;
  private LocalDate issueDate;
  private LocalDate expiryDate;
  private CardStatus status;
  private Instant createdAt;
  private Instant updatedAt;
}
