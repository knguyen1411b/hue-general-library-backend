package org.app.backend.modules.librarycard.dto;

import org.app.backend.modules.librarycard.CardStatus;
import lombok.Data;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class LibraryCardUpdateDTO {
  LocalDate issueDate;
  LocalDate expiryDate;
  CardStatus status;
}
