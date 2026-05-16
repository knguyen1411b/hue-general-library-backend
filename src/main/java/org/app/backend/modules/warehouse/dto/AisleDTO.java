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
import org.app.backend.modules.warehouse.AisleStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AisleDTO {
  UUID id;
  UUID floorId;
  String floorName;
  String name;
  AisleStatus status;
  @Builder.Default List<ShelfDTO> shelves = new ArrayList<>();
}
