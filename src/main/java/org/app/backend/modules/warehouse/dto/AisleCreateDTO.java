package org.app.backend.modules.warehouse.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.app.backend.modules.warehouse.AisleStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AisleCreateDTO {
  @NotNull(message = "Floor ID is required")
  private UUID floorId;

  @NotBlank(message = "Aisle name is required")
  private String name;

  @NotNull(message = "Aisle status is required")
  private AisleStatus status;
}
