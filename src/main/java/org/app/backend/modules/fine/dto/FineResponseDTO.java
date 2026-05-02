package org.app.backend.modules.fine.dto;

import java.util.UUID;
import lombok.Data;
import org.app.backend.modules.fine.enums.FineStatus;

@Data
public class FineResponseDTO {
  UUID id;
  Integer amount;
  String reason;
  FineStatus status;

  // Thông tin kèm theo từ Rental
  UUID rentalId;
  String barcode; // Mã vạch sách
  String bookTitle; // Tên sách
  String readerName; // Tên độc giả
}
