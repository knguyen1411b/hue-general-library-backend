package org.app.backend.modules.category;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum CategoryMessage {
  INDEX_SUCCESS("Lấy danh sách danh mục thành công"),
  SHOW_SUCCESS("Lấy chi tiết danh mục thành công"),
  CREATE_SUCCESS("Tạo mới danh mục thành công"),
  UPDATE_SUCCESS("Cập nhật danh mục thành công"),
  DELETE_SUCCESS("Xóa danh mục thành công"),

  TITLE_TAKEN("Tên danh mục này đã tồn tại trong hệ thống"),
  NOT_FOUND("Không tìm thấy danh mục này");

  String message;
}
