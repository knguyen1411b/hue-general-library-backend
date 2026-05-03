package org.app.backend.modules.warehouse.dto;

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
public class AisleResponseDTO {
  private UUID id;
  private UUID floorId;
  private String floorName;
  private String name;
  private AisleStatus status;
}
