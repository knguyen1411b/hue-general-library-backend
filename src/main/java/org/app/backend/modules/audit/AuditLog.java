package org.app.backend.modules.audit;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.app.backend.modules.audit.enums.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(
    name = "tbl_audit_log",
    indexes = {
      @Index(name = "idx_audit_log_user_id", columnList = "user_id"),
      @Index(name = "idx_audit_log_action", columnList = "action"),
      @Index(name = "idx_audit_log_created_at", columnList = "created_at")
    })
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuditLog {
  @Id
  @GeneratedValue
  @UuidGenerator(style = UuidGenerator.Style.TIME)
  UUID id;

  @Column(name = "user_id")
  UUID userId;

  @Column(length = 100)
  String username;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 100)
  AuditLogAction action;

  @Enumerated(EnumType.STRING)
  @Column(name = "entity_name", nullable = false, length = 100)
  AuditLogEntity entityName;

  @Column(name = "entity_id", length = 100)
  String entityId;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  AuditLogStatus status;

  @Column(columnDefinition = "text")
  String message;

  @Column(name = "ip_address", length = 45)
  String ipAddress;

  @Column(name = "user_agent", length = 500)
  String userAgent;

  @Column(name = "metadata", columnDefinition = "text")
  String metadata;

  @CreationTimestamp
  @Column(name = "created_at", updatable = false, nullable = false)
  Instant createdAt;
}
