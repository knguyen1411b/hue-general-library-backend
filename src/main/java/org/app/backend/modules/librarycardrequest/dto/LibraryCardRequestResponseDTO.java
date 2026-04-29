package org.app.backend.modules.librarycardrequest.dto;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.app.backend.modules.librarycardrequest.LibraryCardRequestStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LibraryCardRequestResponseDTO {
  private UUID id;
  private UUID userId;
  private String userName;
  private LibraryCardRequestStatus status;
  private String deliveryAddress;
  private String note;
  private LocalDateTime createdAt;
}
