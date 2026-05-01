package org.app.backend.modules.rental;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RentalMessage {
  BORROW_SUCCESS("Cho mượn sách thành công"),
  RETURN_SUCCESS("Trả sách thành công"),
  RENEW_SUCCESS("Gia hạn sách thành công"),
  PREVIEW_SUCCESS("Lấy thông tin trả sách thành công"),
  HISTORY_SUCCESS("Lấy lịch sử mượn thành công");

  private final String message;
}
