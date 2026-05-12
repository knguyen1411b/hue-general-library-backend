package org.app.backend.modules.warehouse;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum WarehouseMessage {
  INDEX_SUCCESS("Lấy cấu trúc kho hàng thành công"),
  FLOOR_CREATE_SUCCESS("Tạo tầng kho thành công"),
  FLOOR_UPDATE_SUCCESS("Cập nhật tầng kho thành công"),
  FLOOR_DELETE_SUCCESS("Xóa tầng kho thành công"),
  FLOOR_SHOW_SUCCESS("Lấy thông tin tầng kho thành công"),
  FLOOR_LIST_SUCCESS("Lấy danh sách tầng kho thành công"),
  AISLE_CREATE_SUCCESS("Tạo dãy kệ thành công"),
  AISLE_UPDATE_SUCCESS("Cập nhật dãy kệ thành công"),
  AISLE_DELETE_SUCCESS("Xóa dãy kệ thành công"),
  AISLE_SHOW_SUCCESS("Lấy thông tin dãy kệ thành công"),
  AISLE_LIST_SUCCESS("Lấy danh sách dãy kệ thành công"),
  SHELF_CREATE_SUCCESS("Thêm kệ mới thành công"),
  SHELF_DELETE_SUCCESS("Xóa kệ thành công"),
  SHELF_LIST_SUCCESS("Lấy danh sách kệ thành công"),
  FLOOR_NOT_FOUND("Không tìm thấy tầng kho"),
  AISLE_NOT_FOUND("Không tìm thấy dãy kệ"),
  SHELF_NOT_FOUND("Không tìm thấy kệ"),
  FLOOR_NAME_TAKEN("Tên tầng kho đã tồn tại"),
  AISLE_NAME_TAKEN("Tên dãy kệ đã tồn tại trong tầng kho");

  String message;
}
