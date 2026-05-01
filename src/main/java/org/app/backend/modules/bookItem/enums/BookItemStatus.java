package org.app.backend.modules.bookItem.enums;

public enum BookItemStatus {
  AVAILABLE,
  BORROWED,
  LOST,
  DAMAGED,
  MAINTENANCE, // Đang bảo trì/sửa chữa
  DISCARDED, // Thanh lý hoặc loại bỏ
  PENDING,
  INACTIVE,
  DELETED
}
