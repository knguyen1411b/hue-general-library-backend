package org.app.backend.modules.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ResetPasswordDTO {
  @NotBlank(message = "Token không được để trống")
  String token;

  @NotBlank(message = "Mật khẩu mới không được để trống")
  @Pattern(
      regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{6,}$",
      message = "Mật khẩu phải có chữ và số")
  @Size(min = 6, max = 100, message = "Mật khẩu tối thiểu 6 ký tự")
  String newPassword;
}
