package org.app.backend.modules.bookItem;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum BookItemMessage {
  INDEX_SUCCESS("Lấy danh sách sách thành công"),
  SHOW_SUCCESS("Lấy thông tin sách thành công"),
  CREATE_SUCCESS("Tạo sách thành công"),
  UPDATE_SUCCESS("Cập nhật sách thành công"),
  DELETE_SUCCESS("Xóa sách thành công"),

  NOT_FOUND("Không tìm thấy sách"),
  BOOK_NOT_FOUND("Không tìm thấy đầu sách"),
  SHELF_POSITION_NOT_FOUND("Không tìm thấy vị trí kệ"),
  BARCODE_TAKEN("Mã vạch đã được sử dụng"),
  INVALID_SHELF_POSITION("Vị trí kệ không hợp lệ");

  String message;
}
