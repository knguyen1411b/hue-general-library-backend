package org.app.backend.modules.book.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.app.backend.common.validation.FileValid;
import org.app.backend.modules.book.enums.BookStatus;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookUpdateDTO {
  String title;
  String description;
  Integer price;
  String author;
  String isbn;
  String publishers;
  Integer count;
  Integer publishedYear;
  UUID categoryId;
  BookStatus status;

  @Schema(type = "string", format = "binary")
  @FileValid(
      types = {"image/jpeg", "image/png"},
      maxSize = 5 * 1024 * 1024,
      required = false)
  MultipartFile thumbnail;
}
