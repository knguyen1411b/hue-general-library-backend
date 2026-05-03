package org.app.backend.modules.fine.dto;

import lombok.Data;
import org.app.backend.modules.fine.enums.FineStatus;

@Data
public class FineUpdateDTO {
  Double amount;
  String reason;
  FineStatus status;
}
