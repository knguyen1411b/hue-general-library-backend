package org.app.backend.modules.audit;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum AuditLogMessage {
  INDEX_SUCCESS("Lấy danh sách nhật ký hệ thống thành công"),
  SHOW_SUCCESS("Lấy thông tin nhật ký hệ thống thành công"),
  NOT_FOUND("Không tìm thấy nhật ký hệ thống");
  String message;
}
