package org.app.backend.modules.rental.dto;

import org.app.backend.modules.rental.RentalStatus;
import lombok.Data;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class RentalUpdateDTO {
  LocalDate dueDate;
  LocalDate returnDate;
  RentalStatus status;
}
