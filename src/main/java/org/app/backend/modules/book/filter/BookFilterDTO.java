package org.app.backend.modules.book.filter;

import java.util.UUID;
import lombok.Data;
import org.app.backend.modules.book.BookStatus;

@Data
public class BookFilterDTO {
  String query;
  UUID categoryId;
  BookStatus status;
}
