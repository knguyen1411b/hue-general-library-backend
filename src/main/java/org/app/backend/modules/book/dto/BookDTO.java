package org.app.backend.modules.book.dto;

import java.time.Instant;
import java.util.UUID;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.app.backend.modules.book.BookStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookDTO {
  UUID id;
  String title;
  String author;
  UUID categoryId;
  String isbn;
  String description;
  Integer publishedYear;
  String thumbnailUrl;
  Integer price;
  String publishers;
  Integer count;
  BookStatus status;
  Instant createdAt;
  Instant updatedAt;
}
