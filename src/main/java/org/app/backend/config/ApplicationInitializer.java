package org.app.backend.config;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.app.backend.common.constants.SuperUserProperties;
import org.app.backend.modules.user.User;
import org.app.backend.modules.user.UserRepository;
import org.app.backend.modules.user.UserRole;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Slf4j
@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ApplicationInitializer {
  PasswordEncoder passwordEncoder;
  SuperUserProperties superuser;

  @Bean
  CommandLineRunner initData(UserRepository userRepository) {
    return args -> {
      if (!userRepository.existsByUsername(superuser.getUsername())) {
        userRepository.save(
            User.builder()
                .username(superuser.getUsername())
                .passwordHash(passwordEncoder.encode(superuser.getPassword()))
                .email(superuser.getEmail())
                .fullName(superuser.getFullName())
                .role(UserRole.ADMIN)
                .build());
        log.error("Admin user created with default password, please change it.");
      }
      log.info("Application initialization completed...");
    };
  }
}
