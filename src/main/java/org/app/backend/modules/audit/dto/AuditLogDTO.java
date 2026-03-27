package org.app.backend.modules.audit.dto;

import java.time.Instant;
import java.util.UUID;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.app.backend.modules.audit.enums.AuditLogAction;
import org.app.backend.modules.audit.enums.AuditLogEntity;
import org.app.backend.modules.audit.enums.AuditLogStatus;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuditLogDTO {
  UUID id;
  UUID userId;
  String username;
  AuditLogAction action;
  AuditLogEntity entityName;
  String entityId;
  AuditLogStatus status;
  String message;
  String ipAddress;
  String userAgent;
  String metadata;
  Instant createdAt;
}
