package org.app.backend.modules.rental.dto;

import java.time.LocalDate;
import lombok.Data;
import org.app.backend.modules.rental.RentalStatus;

@Data
public class RentalUpdateDTO {
  LocalDate dueDate;
  LocalDate returnDate;
  RentalStatus status;
}
