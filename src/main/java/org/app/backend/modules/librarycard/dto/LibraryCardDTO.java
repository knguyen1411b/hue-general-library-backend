package org.app.backend.modules.librarycard.dto;

import org.app.backend.modules.librarycard.CardStatus;
import lombok.*;
import java.time.LocalDate;
import java.time.Instant;
import java.util.UUID;

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
