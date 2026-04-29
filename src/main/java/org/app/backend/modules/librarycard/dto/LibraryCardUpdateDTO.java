package org.app.backend.modules.librarycard.dto;

import java.time.LocalDate;
import lombok.Data;
import org.app.backend.modules.librarycard.CardStatus;

@Data
public class LibraryCardUpdateDTO {
  LocalDate issueDate;
  LocalDate expiryDate;
  CardStatus status;
}
