package org.app.backend.modules.user.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.app.backend.modules.user.UserRole;
import org.app.backend.modules.user.UserStatus;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserFilterDTO {
  String q;

  UserStatus status;

  UserRole role;
}
