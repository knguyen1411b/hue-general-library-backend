package org.app.backend.modules.audit;

import java.util.UUID;
import org.app.backend.modules.audit.dto.AuditLogDTO;
import org.app.backend.modules.audit.dto.AuditLogFilterDTO;
import org.app.backend.modules.audit.enums.AuditLogAction;
import org.app.backend.modules.audit.enums.AuditLogEntity;
import org.app.backend.modules.audit.enums.AuditLogStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AuditLogService {
  void log(
      UUID userId,
      String username,
      AuditLogAction action,
      AuditLogEntity entityName,
      String entityId,
      AuditLogStatus status,
      String message);

  Page<AuditLogDTO> findAll(AuditLogFilterDTO filter, Pageable pageable);

  AuditLogDTO findById(UUID id);
}
