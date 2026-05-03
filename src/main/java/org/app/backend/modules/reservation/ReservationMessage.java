package org.app.backend.modules.reservation;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum ReservationMessage {
  RESERVATION_NOT_FOUND("Không tìm thấy phiếu đặt trước"),
  BOOK_NOT_AVAILABLE("Bản sao sách hiện không khả dụng để đặt trước"),
  USER_NOT_FOUND("Không tìm thấy người dùng"),
  BOOK_ITEM_NOT_FOUND("Không tìm thấy bản sao sách");

  String message;
}
