package org.app.backend.modules.audit.dto;

import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.app.backend.modules.audit.enums.AuditLogAction;
import org.app.backend.modules.audit.enums.AuditLogEntity;
import org.app.backend.modules.audit.enums.AuditLogStatus;

@Getter
@Setter
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class AuditLogFilterDTO {
  UUID userId;
  AuditLogAction action;
  AuditLogEntity entityName;
  AuditLogStatus status;
  String q;
}
