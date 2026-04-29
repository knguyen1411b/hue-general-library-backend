package org.app.backend.modules.fine.dto;

import org.app.backend.modules.fine.FineStatus;
import lombok.Data;
import java.util.UUID;

@Data
public class FineUpdateDTO {
  Double amount;
  String reason;
  FineStatus status;
}
