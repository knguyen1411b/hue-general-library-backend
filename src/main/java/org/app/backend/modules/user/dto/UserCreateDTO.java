package org.app.backend.modules.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.app.backend.common.validation.FileValid;
import org.app.backend.modules.user.UserRole;
import org.app.backend.modules.user.UserStatus;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCreateDTO {
  @NotBlank(message = "Tên đăng nhập không được để trống")
  @Size(min = 4, max = 100, message = "Tên đăng nhập phải từ 4 đến 100 ký tự")
  @Pattern(regexp = "^[a-zA-Z0-9._-]+$", message = "Tên đăng nhập không hợp lệ")
  String username;

  @NotBlank(message = "Mật khẩu không được để trống")
  @Pattern(
      regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{6,}$",
      message = "Mật khẩu phải có chữ và số")
  @Size(min = 6, max = 100, message = "Mật khẩu tối thiểu 6 ký tự")
  String password;

  @NotBlank(message = "Họ tên không được để trống")
  @Size(max = 255, message = "Họ tên tối đa 255 ký tự")
  String fullName;

  @NotBlank(message = "Email không được để trống")
  @Email(message = "Email không đúng định dạng")
  @Size(max = 255)
  String email;

  @Pattern(regexp = "^[0-9]{9,15}$", message = "Số điện thoại phải từ 9 đến 15 chữ số")
  String phone;

  boolean gender;

  @Past(message = "Ngày sinh phải là ngày trong quá khứ")
  LocalDate birthday;

  @Size(max = 500, message = "Địa chỉ tối đa 500 ký tự")
  String address;

  @Schema(type = "string", format = "binary")
  @FileValid(
      types = {"image/jpeg", "image/png"},
      maxSize = 5 * 1024 * 1024)
  MultipartFile avatar;

  @NotBlank(message = "Số CCCD/CMND không được để trống")
  @Pattern(regexp = "^[0-9]{9,12}$", message = "Số CCCD/CMND không hợp lệ")
  String identityNumber;

  @NotNull(message = "Ảnh mặt trước CCCD/CMND không được để trống")
  @Schema(type = "string", format = "binary")
  @FileValid(
      types = {"image/jpeg", "image/png"},
      maxSize = 5 * 1024 * 1024,
      required = true)
  MultipartFile identityFront;

  @NotNull(message = "Ảnh mặt sau CCCD/CMND không được để trống")
  @Schema(type = "string", format = "binary")
  @FileValid(
      types = {"image/jpeg", "image/png"},
      maxSize = 5 * 1024 * 1024,
      required = true)
  MultipartFile identityBack;

  @NotNull(message = "Trạng thái không được để trống")
  UserStatus status;

  @NotNull(message = "Vai trò không được để trống")
  UserRole role;
}
