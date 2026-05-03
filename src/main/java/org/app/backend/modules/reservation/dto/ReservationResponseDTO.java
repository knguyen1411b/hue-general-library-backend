package org.app.backend.modules.reservation.dto;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.app.backend.modules.reservation.ReservationStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationResponseDTO {
  private UUID id;
  private UUID userId;
  private String userName;
  private UUID bookItemId;
  private String bookTitle;
  private ReservationStatus status;
  private LocalDateTime reservationDate;
  private LocalDateTime confirmDate;
  private LocalDateTime cancelDate;
}
