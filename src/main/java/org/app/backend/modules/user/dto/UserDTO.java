package org.app.backend.modules.user.dto;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.app.backend.modules.user.UserRole;
import org.app.backend.modules.user.UserStatus;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDTO {
  UUID id;
  String username;

  String fullName;
  String email;
  String phone;
  boolean gender;
  String avatarUrl;
  LocalDate birthday;
  String address;

  String identityNumber;
  String identityFrontUrl;
  String identityBackUrl;

  UserStatus status;
  UserRole role;

  Instant createdAt;
  Instant updatedAt;
  Instant lastLoginAt;
}
