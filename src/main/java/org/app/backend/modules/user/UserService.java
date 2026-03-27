package org.app.backend.modules.user;

import java.util.UUID;
import org.app.backend.modules.auth.security.CustomUserDetails;
import org.app.backend.modules.user.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
  Page<UserDTO> findAll(UserFilterDTO filter, Pageable pageable);

  UserDTO findById(UUID id);

  UserDTO getMe(CustomUserDetails user);

  void updateMe(CustomUserDetails user, MeUpdateDTO dto);

  void create(UserCreateDTO dto, CustomUserDetails actor);

  void update(UUID id, UserUpdateDTO dto, CustomUserDetails actor);

  void delete(UUID id, CustomUserDetails actor);

  CustomUserDetails loadUserById(UUID userId);
}
