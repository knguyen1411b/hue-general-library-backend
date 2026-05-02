package org.app.backend.modules.rental.dto;

import java.time.Instant;
import java.util.UUID;
import lombok.Data;
import org.app.backend.modules.rental.enums.RentalStatus;

@Data
public class RentalResponseDTO {
  UUID id;
  String bookTitle;
  String barcode;
  String readerName;
  Instant borrowDate; // Ngày mượn (Lấy từ base entity createdAt)
  Instant dueDate; // Hạn trả
  Instant returnDate; // Ngày trả thực tế (nếu có)
  RentalStatus status;
}
