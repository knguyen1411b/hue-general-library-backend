package org.app.backend.modules.warehouse.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AddShelfDTO {
  @NotBlank(message = "Shelf name is required")
  String name;

  @Min(value = 1, message = "maxRow must be at least 1")
  int maxRow;

  @Min(value = 1, message = "maxCol must be at least 1")
  int maxCol;

  @NotNull(message = "Aisle ID is required")
  UUID aisleId;
}
