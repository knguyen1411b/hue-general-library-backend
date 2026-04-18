package org.app.backend.modules.book.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.util.UUID;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.app.backend.common.validation.FileValid;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookCreateDTO {
  @NotBlank(message = "Tiêu đề không được để trống")
  @Size(min = 4, max = 255, message = "Tiêu đề phải từ 4 - 255 ký tự")
  @Pattern(regexp = "^[a-zA-Z0-9À-ỹ\\s\\p{Punct}]+$", message = "Tiêu đề chứa ký tự không hợp lệ")
  String title;

  @Size(max = 2000, message = "Mô tả tối đa 2000 ký tự")
  String description;

  @NotNull(message = "Giá tiền không được để trống")
  @Min(value = 0, message = "Giá tiền không được là số âm")
  Integer price;

  @NotBlank(message = "Tên tác giả không được để trống")
  @Size(max = 255, message = "Tên tác giả tối đa 255 ký tự")
  @Pattern(regexp = "^[a-zA-ZÀ-ỹ\\s,.-]+$", message = "Tên tác giả chứa ký tự không hợp lệ")
  String author;

  @NotBlank(message = "Mã ISBN không được để trống")
  @Pattern(regexp = "^[0-9-]+$", message = "Mã ISBN không hợp lệ")
  String isbn;

  @NotBlank(message = "Nhà xuất bản không được để trống")
  @Size(max = 255, message = "Nhà xuất bản tối đa 255 ký tự")
  String publishers;

  @NotNull(message = "Số lượng không được để trống")
  @Min(value = 0, message = "Số lượng đầu sách không được nhỏ hơn 0")
  Integer count;

  @Schema(type = "string", format = "binary")
  @FileValid(
      types = {"image/jpeg", "image/png"},
      maxSize = 5 * 1024 * 1024)
  MultipartFile thumbnail; // Map với file ảnh từ form-data

  @NotNull(message = "Năm xuất bản không được để trống")
  @Min(value = 1000, message = "Năm xuất bản không hợp lệ")
  Integer publishedYear;

  @NotNull(message = "Thể loại đầu sách không được để trống")
  UUID categoryId;
}
