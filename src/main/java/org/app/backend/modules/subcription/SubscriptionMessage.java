package org.app.backend.modules.subcription;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum SubscriptionMessage {
  INDEX_SUCCESS("Lấy danh sách gói đăng ký thành công"),
  SHOW_SUCCESS("Lấy thông tin gói đăng ký thành công"),
  CREATE_SUCCESS("Tạo gói đăng ký thành công"),
  UPDATE_SUCCESS("Cập nhật gói đăng ký thành công"),
  DELETE_SUCCESS("Xóa gói đăng ký thành công"),

  KEY_EXISTS("Mã gói đăng ký đã tồn tại"),
  NAME_EXISTS("Tên gói đăng ký đã tồn tại"),

  NOT_FOUND("Không tìm thấy gói đăng ký");

  String message;
}
