package org.app.backend.modules.bookItem.dto;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.app.backend.modules.bookItem.enums.BookItemStatus;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookItemDTO {
  UUID id;

  String barcode;

  LocalDate importDate;

  BookItemStatus status;

  UUID bookId;

  String bookTitle;

  UUID shelfPositionId;

  String floorName;

  String aisleName;

  String shelfName;

  Instant createdAt;

  Instant updatedAt;
}
