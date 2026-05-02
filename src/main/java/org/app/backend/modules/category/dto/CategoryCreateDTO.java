package org.app.backend.modules.category.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryCreateDTO {
  @NotBlank(message = "Tên danh mục không được để trống")
  @Size(max = 100, message = "Tên danh mục tối đa 100 ký tự")
  String title;
}
