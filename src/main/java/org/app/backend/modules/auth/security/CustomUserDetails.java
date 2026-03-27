package org.app.backend.modules.auth.security;

import java.time.Instant;
import java.time.LocalDate;
import java.util.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.app.backend.modules.user.UserRole;
import org.app.backend.modules.user.UserStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomUserDetails implements UserDetails {
  UUID id;

  String username;
  String password;

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

  boolean passwordChanged;

  Instant createdAt;
  Instant updatedAt;
  Instant lastLoginAt;

  List<GrantedAuthority> authorities;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return authorities != null && !authorities.isEmpty()
        ? authorities
        : List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
  }

  @Override
  public boolean isAccountNonLocked() {
    return status != UserStatus.LOCKED;
  }

  @Override
  public boolean isEnabled() {
    return status == UserStatus.ACTIVE;
  }
}
