package org.app.backend.modules.bookItem.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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
  @Schema(description = "ID cua dau sach", example = "03111dab-f474-460f-91fa-b47f8a4c052f")
  @NotNull(message = "Book ID khong duoc de trong")
  UUID bookId;

  @Schema(description = "Barcode cua ban sach", example = "LL04E6OVSCPA6R6PPPA")
  @NotBlank(message = "Barcode khong duoc de trong")
  @Pattern(regexp = "^[A-Z0-9]{19}$", message = "Barcode phai la chuoi 19 ky tu chu cai in hoa hoac chu so")
  String barcode;

  @Schema(description = "Ngay nhap kho", example = "2026-04-29")
  @NotNull(message = "Ngay nhap kho khong duoc de trong")
  LocalDate importDate;

  @Schema(description = "ID vi tri ke", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
  UUID shelfPositionId;

  @Schema(description = "Thong tin tang", example = "Tang 1")
  String floorName;

  @Schema(description = "Thong tin day", example = "Day A")
  String aisleName;

  @Schema(description = "Thong tin ke", example = "Ke 2")
  String shelfName;
}
