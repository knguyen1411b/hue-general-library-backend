package org.app.backend.modules.warehouse.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.app.backend.modules.warehouse.FloorStatus;
import org.app.backend.modules.warehouse.dto.AisleDTO;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FloorDTO {
  UUID id;
  String name;
  FloorStatus status;
  List<AisleDTO> aisles = new ArrayList<>();
}
