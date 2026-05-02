package org.app.backend.modules.user;

import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.app.backend.common.exception.AppException;
import org.app.backend.core.file.FileService;
import org.app.backend.modules.audit.AuditLogService;
import org.app.backend.modules.audit.enums.*;
import org.app.backend.modules.auth.security.CustomUserDetails;
import org.app.backend.modules.user.dto.*;
import org.jspecify.annotations.NonNull;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserServiceImpl implements UserService {
  UserRepository userRepository;
  PasswordEncoder passwordEncoder;
  FileService fileService;
  ModelMapper modelMapper;
  AuditLogService auditLogService;

  @Override
  @Transactional(readOnly = true)
  public UserDTO getMe(CustomUserDetails user) {
    return modelMapper.map(user, UserDTO.class);
  }

  @Override
  @Transactional
  public void updateMe(CustomUserDetails user, MeUpdateDTO dto) {
    User u =
        userRepository
            .findById(user.getId())
            .orElseThrow(
                () -> new AppException(HttpStatus.NOT_FOUND, UserMessage.NOT_FOUND.getMessage()));

    if (dto.getFullName() != null) {
      u.setFullName(dto.getFullName());
    }

    if (dto.getEmail() != null) {
      validateEmailAvailability(dto.getEmail(), u.getEmail());
      u.setEmail(dto.getEmail());
    }

    if (dto.getPhone() != null) {
      validatePhoneAvailability(dto.getPhone(), u.getPhone());
      u.setPhone(dto.getPhone());
    }

    if (dto.getGender() != null) {
      u.setGender(dto.getGender());
    }

    if (dto.getBirthday() != null) {
      u.setBirthday(dto.getBirthday());
    }

    if (dto.getAddress() != null) {
      u.setAddress(dto.getAddress());
    }

    if (dto.getAvatar() != null) {
      u.setAvatarUrl(fileService.upload(dto.getAvatar(), u.getId().toString() + "_avatar"));
    }

    if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
      u.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
      u.setPasswordChanged(true);
    }

    auditLogService.log(
        user.getId(),
        user.getUsername(),
        AuditLogAction.UPDATE,
        AuditLogEntity.USER,
        u.getId().toString(),
        AuditLogStatus.SUCCESS,
        "Cập nhật thông tin cá nhân thành công");
  }

  @Override
  @Transactional(readOnly = true)
  @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
  public Page<UserDTO> findAll(UserFilterDTO filter, Pageable pageable) {
    Specification<User> spec = UserSpecification.filter(filter);
    return userRepository.findAll(spec, pageable).map(user -> modelMapper.map(user, UserDTO.class));
  }

  @Override
  @Transactional(readOnly = true)
  @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
  public UserDTO findById(UUID id) {
    return userRepository
        .findById(id)
        .map(user -> modelMapper.map(user, UserDTO.class))
        .orElseThrow(
            () -> new AppException(HttpStatus.NOT_FOUND, UserMessage.NOT_FOUND.getMessage()));
  }

  @Override
  @Transactional
  @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
  public void create(@NonNull UserCreateDTO dto, CustomUserDetails actor) {
    validateUsernameAvailability(dto.getUsername());
    validateEmailAvailability(dto.getEmail(), null);
    validatePhoneAvailability(dto.getPhone(), null);
    validateIdentityNumberAvailability(dto.getIdentityNumber());

    User user = modelMapper.map(dto, User.class);
    user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
    userRepository.save(user);

    try {
      if (dto.getAvatar() != null) {
        user.setAvatarUrl(fileService.upload(dto.getAvatar(), user.getId().toString() + "_avatar"));
      }
      user.setIdentityFrontUrl(
          fileService.upload(dto.getIdentityFront(), user.getId().toString() + "_identity_front"));
      user.setIdentityBackUrl(
          fileService.upload(dto.getIdentityBack(), user.getId().toString() + "_identity_back"));
      userRepository.save(user);
    } catch (Exception e) {
      throw new AppException(HttpStatus.BAD_REQUEST, "Lỗi upload file: " + e.getMessage());
    }

    auditLogService.log(
        actor != null ? actor.getId() : null,
        actor != null ? actor.getUsername() : "system",
        AuditLogAction.CREATE,
        AuditLogEntity.USER,
        user.getId().toString(),
        AuditLogStatus.SUCCESS,
        "Tạo người dùng thành công: " + user.getUsername());
  }

  @Override
  @Transactional
  @CacheEvict(value = "userDetails", key = "#id")
  @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
  public void update(UUID id, UserUpdateDTO dto, CustomUserDetails actor) {
    User user =
        userRepository
            .findById(id)
            .orElseThrow(
                () -> new AppException(HttpStatus.NOT_FOUND, UserMessage.NOT_FOUND.getMessage()));

    if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
      user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
      user.setPasswordChanged(true);
    }

    if (dto.getAvatar() != null) {
      user.setAvatarUrl(fileService.upload(dto.getAvatar(), user.getId().toString() + "_avatar"));
    }

    if (dto.getEmail() != null && !dto.getEmail().isBlank()) {
      validateEmailAvailability(dto.getEmail(), user.getEmail());
      user.setEmail(dto.getEmail());
    }

    if (dto.getPhone() != null && !dto.getPhone().isBlank()) {
      validatePhoneAvailability(dto.getPhone(), user.getPhone());
      user.setPhone(dto.getPhone());
    }

    if (dto.getFullName() != null && !dto.getFullName().isBlank()) {
      user.setFullName(dto.getFullName());
    }

    if (dto.getAddress() != null && !dto.getAddress().isBlank()) {
      user.setAddress(dto.getAddress());
    }

    if (dto.getGender() != null) {
      user.setGender(dto.getGender());
    }

    if (dto.getBirthday() != null) {
      user.setBirthday(dto.getBirthday());
    }

    if (dto.getStatus() != null) {
      user.setStatus(dto.getStatus());
    }

    if (dto.getRole() != null) {
      user.setRole(dto.getRole());
    }

    userRepository.save(user);
    auditLogService.log(
        actor != null ? actor.getId() : null,
        actor != null ? actor.getUsername() : "system",
        AuditLogAction.UPDATE,
        AuditLogEntity.USER,
        user.getId().toString(),
        AuditLogStatus.SUCCESS,
        "Cập nhật người dùng thành công: " + user.getUsername());
  }

  @Override
  @Transactional
  @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
  public void delete(UUID id, CustomUserDetails actor) {
    User user =
        userRepository
            .findById(id)
            .orElseThrow(
                () -> new AppException(HttpStatus.NOT_FOUND, UserMessage.NOT_FOUND.getMessage()));
    user.setStatus(UserStatus.DELETED);
    userRepository.save(user);
    auditLogService.log(
        actor != null ? actor.getId() : null,
        actor != null ? actor.getUsername() : "system",
        AuditLogAction.DELETE,
        AuditLogEntity.USER,
        user.getId().toString(),
        AuditLogStatus.SUCCESS,
        "Xóa người dùng thành công: " + user.getUsername());
  }

  @Override
  @Transactional(readOnly = true)
  @Cacheable(value = "userDetails", key = "#id")
  public CustomUserDetails loadUserById(UUID id) throws UsernameNotFoundException {
    User user =
        userRepository
            .findById(id)
            .orElseThrow(() -> new UsernameNotFoundException(UserMessage.NOT_FOUND.getMessage()));
    CustomUserDetails userDetails = modelMapper.map(user, CustomUserDetails.class);
    userDetails.setAuthorities(
        List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())));
    return userDetails;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    return null;
  }

  private void validateUsernameAvailability(String username) {
    if (userRepository.existsByUsername(username)) {
      throw new AppException(HttpStatus.CONFLICT, UserMessage.USERNAME_TAKEN.getMessage());
    }
  }

  private void validateEmailAvailability(String email, String currentEmail) {
    if (email == null || email.isBlank() || email.equals(currentEmail)) {
      return;
    }

    if (userRepository.existsByEmail(email)) {
      throw new AppException(HttpStatus.CONFLICT, UserMessage.EMAIL_TAKEN.getMessage());
    }
  }

  private void validatePhoneAvailability(String phone, String currentPhone) {
    if (phone == null || phone.isBlank() || phone.equals(currentPhone)) {
      return;
    }

    if (userRepository.existsByPhone(phone)) {
      throw new AppException(HttpStatus.CONFLICT, UserMessage.PHONE_TAKEN.getMessage());
    }
  }

  private void validateIdentityNumberAvailability(String identityNumber) {
    if (userRepository.existsByIdentityNumber(identityNumber)) {
      throw new AppException(HttpStatus.CONFLICT, UserMessage.IDENTITY_NUMBER_TAKEN.getMessage());
    }
  }
}
