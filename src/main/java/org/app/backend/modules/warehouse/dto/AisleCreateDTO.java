package org.app.backend.modules.warehouse.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.app.backend.modules.warehouse.AisleStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AisleCreateDTO {
  @NotNull(message = "Floor ID is required")
  UUID floorId;

  @NotBlank(message = "Aisle name is required")
  String name;

  @NotNull(message = "Aisle status is required")
  AisleStatus status;
}
