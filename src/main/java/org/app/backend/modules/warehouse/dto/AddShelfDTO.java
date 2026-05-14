package org.app.backend.modules.warehouse.dto;

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

  @NotNull(message = "Aisle ID is required")
  UUID aisleId;
}
