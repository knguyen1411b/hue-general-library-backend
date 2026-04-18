package org.app.backend.modules.warehouse.dto;

import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AddShelfDTO {
  String name;
  int maxRow;
  int maxCol;
  UUID aisleId;
}
