package org.app.backend.modules.librarycardrequest.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LibraryCardRequestCreateDTO {
  @NotBlank(message = "Delivery address is required")
  private String deliveryAddress;

  private String note;
}
