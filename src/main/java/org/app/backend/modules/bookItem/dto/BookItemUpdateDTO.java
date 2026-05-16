package org.app.backend.modules.bookItem.dto;

import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.app.backend.modules.bookItem.enums.BookItemStatus;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookItemUpdateDTO {
  @Pattern(
      regexp = "^[A-Z0-9]{19}$",
      message = "Barcode phai la chuoi 19 ky tu chu cai in hoa hoac chu so")
  String barcode;

  LocalDate importDate;

  UUID shelfPositionId;

  String floorName;

  String aisleName;

  String shelfName;

  BookItemStatus status;
}
