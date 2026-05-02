package org.app.backend.modules.warehouse;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum WarehouseMessage {
  INDEX_SUCCESS("Lấy danh sách kệ thành công"),
  CREATE_SUCCESS("Tạo kệ thành công"),
  DELETE_SUCCESS("Xóa kệ thành công"),
  ALREADY_EXIST("Kệ đã tồn tại"),
  CANNOT_DELETE("Không thể xóa kệ vì còn sách bên trong");

  String message;
}
