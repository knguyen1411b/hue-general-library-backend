package org.app.backend.modules.user;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UserRepository extends JpaRepository<User, UUID>, JpaSpecificationExecutor<User> {
  Optional<User> findByUsername(String username);

  Optional<User> findByRefreshTokenHash(String refreshTokenHash);

  boolean existsByUsername(String username);

  boolean existsByEmail(String email);

  boolean existsByPhone(String phone);

  boolean existsByIdentityNumber(String identityNumber);

  Optional<User> findByEmail(String email);

  Optional<User> findByResetPasswordTokenHash(String resetPasswordTokenHash);
}
