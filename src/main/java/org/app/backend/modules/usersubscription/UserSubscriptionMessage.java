package org.app.backend.modules.usersubscription;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum UserSubscriptionMessage {
  CREATED_SUCCESS("Tạo đăng ký gói cho người dùng thành công"),
  UPDATED_SUCCESS("Cập nhật đăng ký gói cho người dùng thành công"),
  DELETED_SUCCESS("Xóa đăng ký gói cho người dùng thành công"),
  FOUND_SUCCESS("Lấy thông tin đăng ký gói cho người dùng thành công"),
  LIST_SUCCESS("Lấy danh sách đăng ký gói cho người dùng thành công"),

  NOT_FOUND("Không tìm thấy đăng ký gói cho người dùng"),
  INVALID_STATUS("Trạng thái đăng ký không hợp lệ"),
  USER_NOT_FOUND("Không tìm thấy người dùng"),
  SUBSCRIPTION_NOT_FOUND("Không tìm thấy gói đăng ký"),
  ALREADY_EXISTS("Đăng ký gói cho người dùng đã tồn tại"),
  END_DATE_BEFORE_START_DATE("Ngày kết thúc phải sau ngày bắt đầu"),
  MAX_BOOKS_EXCEEDED("Vượt quá số lượng sách được mượn tối đa"),
  SUBSCRIPTION_EXPIRED("Đăng ký gói đã hết hạn"),
  SUBSCRIPTION_CANCELED("Đăng ký gói đã bị hủy"),

  VALIDATION_START_DATE_REQUIRED("Ngày bắt đầu không được để trống"),
  VALIDATION_END_DATE_REQUIRED("Ngày kết thúc không được để trống"),
  VALIDATION_USER_REQUIRED("Người dùng không được để trống"),
  VALIDATION_SUBSCRIPTION_REQUIRED("Gói đăng ký không được để trống"),
  VALIDATION_MAX_BOOKS_REQUIRED("Số sách tối đa không được để trống"),
  VALIDATION_PRICE_REQUIRED("Giá gói không được để trống"),

  LOG_CREATING("Đang tạo đăng ký gói cho người dùng"),
  LOG_UPDATING("Đang cập nhật đăng ký gói cho người dùng"),
  LOG_DELETING("Đang xóa đăng ký gói cho người dùng"),
  LOG_FOUND("Đã tìm thấy đăng ký gói cho người dùng"),
  LOG_LISTING("Đang lấy danh sách đăng ký gói cho người dùng"),
  LOG_CHECKING_STATUS("Đang kiểm tra trạng thái đăng ký gói cho người dùng");

  String message;
}
