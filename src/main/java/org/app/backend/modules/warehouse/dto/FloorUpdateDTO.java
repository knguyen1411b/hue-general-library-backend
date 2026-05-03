package org.app.backend.modules.warehouse.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.app.backend.modules.warehouse.FloorStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FloorUpdateDTO {
  private String name;
  private FloorStatus status;
}
