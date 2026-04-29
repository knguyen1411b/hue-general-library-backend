package org.app.backend.modules.warehouse.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.app.backend.modules.warehouse.FloorStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FloorCreateDTO {
  @NotBlank(message = "Floor name is required")
  private String name;

  @NotNull(message = "Floor status is required")
  private FloorStatus status;
}
