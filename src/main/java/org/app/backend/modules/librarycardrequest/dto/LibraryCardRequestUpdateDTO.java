package org.app.backend.modules.librarycardrequest.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.app.backend.modules.librarycardrequest.LibraryCardRequestStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LibraryCardRequestUpdateDTO {
  @NotNull(message = "Status is required")
  private LibraryCardRequestStatus status;

  private String note;
}
