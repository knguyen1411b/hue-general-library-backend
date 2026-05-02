package org.app.backend.modules.book;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum BookMessage {
  INDEX_SUCCESS("Lấy danh sách đầu sách thành công"),
  SHOW_SUCCESS("Lấy thông tin đầu sách thành công"),
  CREATE_SUCCESS("Tạo đầu sách thành công"),
  UPDATE_SUCCESS("Cập nhật đầu sách thành công"),
  DELETE_SUCCESS("Xóa đầu sách thành công"),

  NOT_FOUND("Không tìm thấy đầu sách"),
  ISBN_EXISTS("Mã ISBN đã tồn tại"),
  CATEGORY_NOT_FOUND("Danh mục không tồn tại");

  String message;
}
