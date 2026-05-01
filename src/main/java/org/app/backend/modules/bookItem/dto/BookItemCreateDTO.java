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

  @Schema(description = "Barcode của bản sách", example = "LL04E6OVSCPA6R6PPPC")
  @NotBlank(message = "Barcode không được để trống")
  @Pattern(regexp = "^[A-Z0-9-]{5,20}$", message = "Barcode không hợp lệ")
  String barcode;

  @Schema(description = "Ngày nhập kho", example = "2026-04-29")
  @NotNull(message = "Ngày nhập kho không được để trống")
  LocalDate importDate;

  @Schema(description = "ID vị trí kệ (tùy chọn)", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
  UUID shelfPositionId;
}
