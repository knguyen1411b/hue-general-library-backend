package org.app.backend.modules.subscription.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SubscriptionCreateDTO {

  @NotBlank(message = "Mã gói không được để trống")
  @Size(min = 1, max = 50, message = "Mã gói từ 1 đến 50 ký tự")
  @Pattern(regexp = "^[A-Z0-9_]+$", message = "Mã gói chỉ được chứa chữ hoa, số và dấu gạch dưới")
  String key;

  @NotBlank(message = "Tên gói không được để trống")
  @Size(min = 1, max = 255, message = "Tên gói tối đa 255 ký tự")
  @Pattern(
      regexp = "^[\\p{L}0-9\\s\\-]+$",
      message = "Tên gói chỉ được chứa chữ cái, số, khoảng trắng và dấu gạch ngang")
  String name;

  @NotNull(message = "Giới hạn mượn tối đa không được để trống")
  @Min(value = 1, message = "Giới hạn mượn tối đa phải lớn hơn 0")
  @Max(value = 1000, message = "Giới hạn mượn tối đa không được vượt quá 1000 quyển")
  Integer maxBooks;

  @NotNull(message = "Phí không được để trống")
  @Min(value = 0, message = "Phí phải lớn hơn hoặc bằng 0")
  @Max(value = 100000000, message = "Phí không được vượt quá 100,000,000 đồng")
  Integer price;

  @NotNull(message = "Thời hạn ngày không được để trống")
  @Min(value = 1, message = "Thời hạn phải lớn hơn 0")
  @Max(value = 3650, message = "Thời hạn không được vượt quá 3650 ngày")
  Integer durationDays;

  @NotNull(message = "Phí trễ hạn không được để trống")
  @Min(value = 0, message = "Phí trễ hạn phải lớn hơn hoặc bằng 0")
  @Max(value = 1000000, message = "Phí trễ hạn không được vượt quá 1,000,000 đồng/ngày")
  Integer overdueFeePerDay;

  @NotNull(message = "Giới hạn gia hạn không được để trống")
  @Min(value = 0, message = "Giới hạn gia hạn phải lớn hơn hoặc bằng 0")
  @Max(value = 100, message = "Giới hạn gia hạn không được vượt quá 100 lần")
  Integer maxRenewals;

  @NotNull(message = "Tỷ lệ bồi thường không được để trống")
  @Min(value = 0, message = "Tỷ lệ bồi thường phải lớn hơn hoặc bằng 0")
  @Max(value = 300, message = "Tỷ lệ bồi thường không được vượt quá 300%")
  Integer compensationRate;
}
