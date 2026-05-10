package org.app.backend.modules.bookItem.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookItemCreateDTO {
  @Schema(description = "ID của đầu sách", example = "03111dab-f474-460f-91fa-b47f8a4c052f")
  @NotNull(message = "Book ID không được để trống")
  UUID bookId;

  @Schema(description = "Barcode của bản sách", example = "LL04E6OVSCPA6R6PPPA")
  @NotBlank(message = "Barcode không được để trống")
  @Pattern(regexp = "^[A-Z]{19}$", message = "Barcode phải là chuỗi 19 ký tự chữ cái in hoa")
  String barcode;

  @Schema(description = "Ngày nhập kho", example = "2026-04-29")
  @NotNull(message = "Ngày nhập kho không được để trống")
  LocalDate importDate;

  @Schema(description = "ID vị trí kệ (tùy chọn)", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
  UUID shelfPositionId; // Cho phép null khi tạo mới

  @Schema(description = "Thông tin tầng", example = "Tầng 1")
  String floorName; // Optional, derived from shelfPosition if not provided

  @Schema(description = "Thông tin dãy", example = "Dãy A")
  String aisleName; // Optional, derived from shelfPosition if not provided

  @Schema(description = "Thông tin Kệ", example = "Kệ 2")
  String shelfName; // Optional, derived from shelfPosition if not provided

  @Schema(description = "Vị trí hàng", example = "1")
  Integer rowIndex; // Optional, derived from shelfPosition if not provided

  @Schema(description = "Vị trí cột", example = "3")
  Integer colIndex; // Optional, derived from shelfPosition if not provided
}
