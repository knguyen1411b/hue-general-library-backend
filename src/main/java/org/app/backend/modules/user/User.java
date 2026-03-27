package org.app.backend.modules.user;

import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(
    name = "tbl_user",
    indexes = {
      @Index(name = "idx_user_status", columnList = "status"),
      @Index(name = "idx_user_role", columnList = "role"),
      @Index(name = "idx_user_refresh_token_hash", columnList = "refresh_token_hash"),
      @Index(name = "idx_user_reset_password_token_hash", columnList = "reset_password_token_hash")
    })
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {
  @Id
  @GeneratedValue
  @UuidGenerator(style = UuidGenerator.Style.TIME)
  UUID id;

  @Column(length = 100, nullable = false, unique = true)
  String username;

  @Column(name = "password_hash", nullable = false, length = 255)
  String passwordHash;

  @Column(name = "full_name", length = 255)
  String fullName;

  @Column(unique = true, length = 255)
  String email;

  @Column(unique = true, length = 20)
  String phone;

  @Column(name = "avatar_url", length = 500)
  String avatarUrl;

  LocalDate birthday;

  boolean gender;

  @Column(name = "identity_number", unique = true, length = 20)
  String identityNumber;

  @Column(name = "identity_front_url", length = 500)
  String identityFrontUrl;

  @Column(name = "identity_back_url", length = 500)
  String identityBackUrl;

  @Column(columnDefinition = "text")
  String address;

  @Builder.Default
  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  UserStatus status = UserStatus.ACTIVE;

  @Builder.Default
  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  UserRole role = UserRole.USER;

  @Builder.Default
  @Column(name = "password_changed", nullable = false)
  boolean passwordChanged = false;

  @CreationTimestamp
  @Column(name = "created_at", updatable = false, nullable = false)
  Instant createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at", nullable = false)
  Instant updatedAt;

  @Column(name = "last_login_at")
  Instant lastLoginAt;

  @Column(name = "refresh_token_hash")
  String refreshTokenHash;

  @Column(name = "refresh_token_expired_at")
  Instant refreshTokenExpiredAt;

  @Column(name = "reset_password_token_hash")
  String resetPasswordTokenHash;

  @Column(name = "reset_password_token_expired_at")
  Instant resetPasswordTokenExpiredAt;
}
