package org.app.backend.modules.bookItem.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.UUID;
import lombok.Data;

@Data
public class BookItemCreateDTO {
  @NotNull(message = "Book ID không được để trống")
  UUID bookId;

  @NotBlank(message = "Barcode không được để trống")
  @Pattern(regexp = "^[A-Z0-9-]{5,20}$", message = "Barcode không hợp lệ")
  String barcode;

  @NotNull(message = "Ngày nhập kho không được để trống")
  LocalDate importDate;

  UUID shelfPositionId; // Cho phép null khi tạo mới

  String floorName; // Optional, derived from shelfPosition if not provided

  String aisleName; // Optional, derived from shelfPosition if not provided

  String shelfName; // Optional, derived from shelfPosition if not provided

  Integer rowIndex; // Optional, derived from shelfPosition if not provided

  Integer colIndex; // Optional, derived from shelfPosition if not provided
}
