package org.app.backend.modules.audit;

import java.util.UUID;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.app.backend.common.exception.AppException;
import org.app.backend.modules.audit.dto.AuditLogDTO;
import org.app.backend.modules.audit.dto.AuditLogFilterDTO;
import org.app.backend.modules.audit.enums.AuditLogAction;
import org.app.backend.modules.audit.enums.AuditLogEntity;
import org.app.backend.modules.audit.enums.AuditLogStatus;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuditLogServiceImpl implements AuditLogService {
  AuditLogRepository auditLogRepository;
  HttpServletRequest request;
  ModelMapper modelMapper;

  @Override
  @Transactional
  public void log(
      UUID userId,
      String username,
      AuditLogAction action,
      AuditLogEntity entityName,
      String entityId,
      AuditLogStatus status,
      String message) {
    AuditLog auditLog =
        AuditLog.builder()
            .userId(userId)
            .username(username)
            .action(action)
            .entityName(entityName)
            .entityId(entityId)
            .status(status)
            .message(message)
            .ipAddress(getClientIp())
            .userAgent(getUserAgent())
            .build();
    auditLogRepository.save(auditLog);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<AuditLogDTO> findAll(AuditLogFilterDTO filter, Pageable pageable) {
    Specification<AuditLog> spec = AuditLogSpecification.filter(filter);
    return auditLogRepository
        .findAll(spec, pageable)
        .map(auditLog -> modelMapper.map(auditLog, AuditLogDTO.class));
  }

  @Override
  @Transactional(readOnly = true)
  public AuditLogDTO findById(UUID id) {
    return auditLogRepository
        .findById(id)
        .map(auditLog -> modelMapper.map(auditLog, AuditLogDTO.class))
        .orElseThrow(
            () -> new AppException(HttpStatus.NOT_FOUND, AuditLogMessage.NOT_FOUND.getMessage()));
  }

  private String getClientIp() {
    String xfHeader = request.getHeader("X-Forwarded-For");
    if (xfHeader == null || xfHeader.isEmpty()) {
      return request.getRemoteAddr();
    }
    return xfHeader.split(",")[0];
  }

  private String getUserAgent() {
    return request.getHeader("User-Agent");
  }
}
