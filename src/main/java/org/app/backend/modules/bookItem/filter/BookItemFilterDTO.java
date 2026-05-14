package org.app.backend.modules.bookItem.filter;

import java.util.UUID;
import lombok.Data;
import org.app.backend.modules.bookItem.enums.BookItemStatus;

@Data
public class BookItemFilterDTO {
  String barcode;
  UUID bookId;
  BookItemStatus status;
  UUID shelfPositionId;
  String floorName;
  String aisleName;
  String shelfName;
}
