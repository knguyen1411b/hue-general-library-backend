package org.app.backend.modules.category.dto;

import java.time.Instant;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryDTO {
  UUID id;
  String title;
  Instant createdAt;
  Instant updatedAt;
}
