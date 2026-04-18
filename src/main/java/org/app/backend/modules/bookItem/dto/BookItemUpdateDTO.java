package org.app.backend.modules.bookItem.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.app.backend.modules.bookItem.BookItemStatus;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookItemUpdateDTO {
  @NotBlank(message = "Barcode không được để trống")
  @Pattern(regexp = "^[A-Z0-9-]{5,20}$", message = "Barcode không hợp lệ")
  String barcode;

  @NotNull(message = "Ngày nhập kho không được để trống")
  LocalDate importDate;

  UUID shelfPositionId;

  BookItemStatus status;
}
