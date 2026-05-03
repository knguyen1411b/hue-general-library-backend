package org.app.backend.modules.book.dto;

import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.app.backend.modules.book.enums.BookStatus;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookFilterDTO {
  String query;
  UUID categoryId;
  BookStatus status;
}
