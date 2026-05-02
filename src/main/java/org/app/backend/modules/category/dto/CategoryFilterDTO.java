package org.app.backend.modules.category.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryFilterDTO {
  String title; // Tìm kiếm theo tên thể loại
}
