package org.app.backend.modules.rental.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Data;
import org.app.backend.modules.rental.enums.BookCondition;

@Data
public class RentalReturnDTO {
  @NotEmpty(message = "Danh sách mã vạch không được để trống")
  List<String> barcodes;

  @NotNull(message = "Tình trạng sách không được để trống")
  BookCondition condition;

  String physicalConditionNotes; // Ghi chú về tình trạng vật lý (tùy chọn)

  @NotNull(message = "Xác nhận trả sách là bắt buộc")
  boolean confirmReturn; // Xác nhận trả sách
}
