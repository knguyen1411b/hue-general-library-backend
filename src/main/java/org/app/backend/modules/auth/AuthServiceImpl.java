package org.app.backend.modules.auth;

import java.time.Instant;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.app.backend.common.constants.JwtProperties;
import org.app.backend.common.exception.AppException;
import org.app.backend.core.file.FileService;
import org.app.backend.core.mail.MailService;
import org.app.backend.modules.audit.AuditLogService;
import org.app.backend.modules.audit.enums.*;
import org.app.backend.modules.auth.dto.*;
import org.app.backend.modules.auth.security.AuthUtils;
import org.app.backend.modules.auth.security.JwtService;
import org.app.backend.modules.user.*;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthServiceImpl implements AuthService {
  JwtService jwtService;
  JwtProperties jwtProperties;
  UserRepository userRepository;
  PasswordEncoder passwordEncoder;
  MailService mailService;
  FileService fileService;
  ModelMapper modelMapper;
  AuditLogService auditLogService;

  @Override
  @Transactional
  public TokenDTO signIn(SignInDTO dto) {
    User user = findUserByUsername(dto.getUsername());

    if (!passwordEncoder.matches(dto.getPassword(), user.getPasswordHash())) {
      auditLogService.log(
          user.getId(),
          user.getUsername(),
          AuditLogAction.LOGIN,
          AuditLogEntity.AUTH,
          user.getId().toString(),
          AuditLogStatus.FAILED,
          "Đăng nhập thất bại do mật khẩu không đúng");
      throw new AppException(HttpStatus.UNAUTHORIZED, AuthMessage.INVALID_CREDENTIALS.getMessage());
    }

    validateUserCanAuthenticate(user);

    Instant now = Instant.now();

    String accessToken = jwtService.generateAccessToken(user.getId());
    String refreshTokenValue = AuthUtils.generateToken(128);
    String refreshTokenHash = AuthUtils.hashToken(refreshTokenValue);

    user.setRefreshTokenHash(refreshTokenHash);
    user.setRefreshTokenExpiredAt(now.plusMillis(jwtProperties.getRefreshTokenExpiration()));
    user.setLastLoginAt(now);

    userRepository.save(user);
    auditLogService.log(
        user.getId(),
        user.getUsername(),
        AuditLogAction.LOGIN,
        AuditLogEntity.AUTH,
        user.getId().toString(),
        AuditLogStatus.SUCCESS,
        "Đăng nhập thành công");

    return TokenDTO.builder()
        .accessToken(accessToken)
        .refreshToken(refreshTokenValue)
        .accessTokenExpiration(jwtProperties.getAccessTokenExpiration())
        .refreshTokenExpiration(jwtProperties.getRefreshTokenExpiration())
        .build();
  }

  @Override
  @Transactional
  public TokenDTO refresh(RefreshTokenDTO dto) {
    if (dto == null || dto.getRefreshToken() == null || dto.getRefreshToken().isBlank()) {
      auditLogService.log(
          null,
          "anonymous",
          AuditLogAction.REFRESH_TOKEN,
          AuditLogEntity.AUTH,
          null,
          AuditLogStatus.FAILED,
          "Làm mới token thất bại do refresh token rỗng");
      throw new AppException(
          HttpStatus.UNAUTHORIZED, AuthMessage.INVALID_REFRESH_TOKEN.getMessage());
    }

    String incomingRefreshToken = dto.getRefreshToken().trim();
    String incomingTokenHash = AuthUtils.hashToken(incomingRefreshToken);

    User user =
        userRepository
            .findByRefreshTokenHash(incomingTokenHash)
            .orElseThrow(
                () -> {
                  auditLogService.log(
                      null,
                      "anonymous",
                      AuditLogAction.REFRESH_TOKEN,
                      AuditLogEntity.AUTH,
                      null,
                      AuditLogStatus.FAILED,
                      "Làm mới token thất bại do refresh token không hợp lệ");
                  return new AppException(
                      HttpStatus.UNAUTHORIZED, AuthMessage.INVALID_REFRESH_TOKEN.getMessage());
                });

    if (user.getStatus() == UserStatus.INACTIVE
        || user.getStatus() == UserStatus.LOCKED
        || user.getStatus() == UserStatus.DELETED) {
      user.setRefreshTokenHash(null);
      user.setRefreshTokenExpiredAt(null);
      userRepository.save(user);
      auditLogService.log(
          user.getId(),
          user.getUsername(),
          AuditLogAction.REFRESH_TOKEN,
          AuditLogEntity.AUTH,
          user.getId().toString(),
          AuditLogStatus.FAILED,
          "Làm mới token thất bại do tài khoản không hợp lệ");
      throw new AppException(
          HttpStatus.UNAUTHORIZED, AuthMessage.INVALID_REFRESH_TOKEN.getMessage());
    }

    Instant now = Instant.now();
    Instant refreshTokenExpiredAt = user.getRefreshTokenExpiredAt();

    if (refreshTokenExpiredAt == null || !refreshTokenExpiredAt.isAfter(now)) {
      user.setRefreshTokenHash(null);
      user.setRefreshTokenExpiredAt(null);
      userRepository.save(user);
      auditLogService.log(
          user.getId(),
          user.getUsername(),
          AuditLogAction.REFRESH_TOKEN,
          AuditLogEntity.AUTH,
          user.getId().toString(),
          AuditLogStatus.FAILED,
          "Làm mới token thất bại do refresh token hết hạn");

      throw new AppException(
          HttpStatus.UNAUTHORIZED, AuthMessage.EXPIRED_REFRESH_TOKEN.getMessage());
    }

    String newAccessToken = jwtService.generateAccessToken(user.getId());

    String newRefreshToken = AuthUtils.generateToken(128);
    String newRefreshTokenHash = AuthUtils.hashToken(newRefreshToken);
    Instant newRefreshTokenExpiredAt = now.plusMillis(jwtProperties.getRefreshTokenExpiration());

    user.setRefreshTokenHash(newRefreshTokenHash);
    user.setRefreshTokenExpiredAt(newRefreshTokenExpiredAt);
    userRepository.save(user);
    auditLogService.log(
        user.getId(),
        user.getUsername(),
        AuditLogAction.REFRESH_TOKEN,
        AuditLogEntity.AUTH,
        user.getId().toString(),
        AuditLogStatus.SUCCESS,
        "Làm mới thành công");

    return TokenDTO.builder()
        .accessToken(newAccessToken)
        .refreshToken(newRefreshToken)
        .accessTokenExpiration(jwtProperties.getAccessTokenExpiration())
        .refreshTokenExpiration(jwtProperties.getRefreshTokenExpiration())
        .build();
  }

  @Override
  @Transactional
  public void signOut(UUID userId) {
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(
                () -> new AppException(HttpStatus.NOT_FOUND, UserMessage.NOT_FOUND.getMessage()));
    user.setRefreshTokenHash(null);
    user.setRefreshTokenExpiredAt(null);
    auditLogService.log(
        user.getId(),
        user.getUsername(),
        AuditLogAction.LOGOUT,
        AuditLogEntity.AUTH,
        user.getId().toString(),
        AuditLogStatus.SUCCESS,
        "Đăng xuất thành công");
  }

  @Override
  @Transactional
  public void signUp(SignUpDTO dto) {
    if (userRepository.existsByUsername(dto.getUsername())) {
      throw new AppException(HttpStatus.CONFLICT, UserMessage.USERNAME_TAKEN.getMessage());
    }

    if (userRepository.existsByEmail(dto.getEmail())) {
      throw new AppException(HttpStatus.CONFLICT, UserMessage.EMAIL_TAKEN.getMessage());
    }

    if (userRepository.existsByPhone(dto.getPhone())) {
      throw new AppException(HttpStatus.CONFLICT, UserMessage.PHONE_TAKEN.getMessage());
    }

    if (userRepository.existsByIdentityNumber(dto.getIdentityNumber())) {
      throw new AppException(HttpStatus.CONFLICT, UserMessage.IDENTITY_NUMBER_TAKEN.getMessage());
    }

    User newUser = modelMapper.map(dto, User.class);
    newUser.setPasswordHash(passwordEncoder.encode(dto.getPassword()));

    userRepository.saveAndFlush(newUser);

    if (dto.getAvatar() != null) {
      newUser.setAvatarUrl(
          fileService.upload(dto.getAvatar(), newUser.getId().toString() + "_avatar"));
    }
    newUser.setIdentityFrontUrl(
        fileService.upload(dto.getIdentityFront(), newUser.getId().toString() + "_identity_front"));
    newUser.setIdentityBackUrl(
        fileService.upload(dto.getIdentityBack(), newUser.getId().toString() + "_identity_back"));
    userRepository.save(newUser);
    auditLogService.log(
        newUser.getId(),
        newUser.getUsername(),
        AuditLogAction.SIGN_UP,
        AuditLogEntity.USER,
        newUser.getId().toString(),
        AuditLogStatus.SUCCESS,
        "Đăng ký tài khoản thành công");
  }

  @Override
  @Transactional
  public void changePassword(UUID userId, ChangePasswordDTO dto) {
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(
                () -> new AppException(HttpStatus.NOT_FOUND, UserMessage.NOT_FOUND.getMessage()));
    if (!passwordEncoder.matches(dto.getCurrentPassword(), user.getPasswordHash())) {
      throw new AppException(HttpStatus.UNAUTHORIZED, AuthMessage.INVALID_CREDENTIALS.getMessage());
    }

    user.setPasswordHash(passwordEncoder.encode(dto.getNewPassword()));
    user.setPasswordChanged(true);
    auditLogService.log(
        user.getId(),
        user.getUsername(),
        AuditLogAction.CHANGE_PASSWORD,
        AuditLogEntity.USER,
        user.getId().toString(),
        AuditLogStatus.SUCCESS,
        "Đổi mật khẩu thành công");
  }

  @Override
  public void forgotPassword(ForgotPasswordDTO dto) {
    User user = userRepository.findByEmail(dto.getEmail()).orElse(null);
    if (user == null) {
      auditLogService.log(
          null,
          dto.getEmail(),
          AuditLogAction.FORGOT_PASSWORD,
          AuditLogEntity.AUTH,
          null,
          AuditLogStatus.FAILED,
          "Gửi yêu cầu quên mật khẩu cho email không tồn tại");
      return;
    }

    String token = AuthUtils.generateToken(64);
    String tokenHash = AuthUtils.hashToken(token);

    user.setResetPasswordTokenHash(tokenHash);
    user.setResetPasswordTokenExpiredAt(Instant.now().plusSeconds(15 * 60));

    userRepository.save(user);

    mailService.sendForgotPasswordMail(user.getEmail(), token);
    auditLogService.log(
        user.getId(),
        user.getUsername(),
        AuditLogAction.FORGOT_PASSWORD,
        AuditLogEntity.USER,
        user.getId().toString(),
        AuditLogStatus.SUCCESS,
        "Gửi email đặt lại mật khẩu thành công");
  }

  @Override
  @Transactional
  public void resetPassword(ResetPasswordDTO dto) {
    String tokenHash = AuthUtils.hashToken(dto.getToken());

    User user =
        userRepository
            .findByResetPasswordTokenHash(tokenHash)
            .orElseThrow(this::invalidResetPasswordTokenException);

    Instant now = Instant.now();
    Instant resetPasswordTokenExpiredAt = user.getResetPasswordTokenExpiredAt();
    if (resetPasswordTokenExpiredAt == null || !resetPasswordTokenExpiredAt.isAfter(now)) {
      user.setResetPasswordTokenHash(null);
      user.setResetPasswordTokenExpiredAt(null);
      userRepository.save(user);
      auditLogService.log(
          user.getId(),
          user.getUsername(),
          AuditLogAction.RESET_PASSWORD,
          AuditLogEntity.USER,
          user.getId().toString(),
          AuditLogStatus.FAILED,
          "Đặt lại mật khẩu thất bại do token hết hạn");
      throw new AppException(
          HttpStatus.UNAUTHORIZED, AuthMessage.EXPIRED_RESET_PASSWORD_TOKEN.getMessage());
    }

    user.setPasswordHash(passwordEncoder.encode(dto.getNewPassword()));
    user.setPasswordChanged(true);

    user.setResetPasswordTokenHash(null);
    user.setResetPasswordTokenExpiredAt(null);

    user.setRefreshTokenHash(null);
    user.setRefreshTokenExpiredAt(null);

    userRepository.save(user);
    auditLogService.log(
        user.getId(),
        user.getUsername(),
        AuditLogAction.RESET_PASSWORD,
        AuditLogEntity.USER,
        user.getId().toString(),
        AuditLogStatus.SUCCESS,
        "Đặt lại mật khẩu thành công");
  }

  private User findUserByUsername(String username) {
    return userRepository
        .findByUsername(username)
        .orElseThrow(() -> invalidCredentialsException(username));
  }

  private void validateUserCanAuthenticate(User user) {
    if (user.getStatus() == UserStatus.INACTIVE) {
      auditLogService.log(
          user.getId(),
          user.getUsername(),
          AuditLogAction.LOGIN,
          AuditLogEntity.USER,
          user.getId().toString(),
          AuditLogStatus.FAILED,
          "Đăng nhập thất bại do tài khoản chưa kích hoạt");
      throw new AppException(HttpStatus.FORBIDDEN, AuthMessage.INACTIVE.getMessage());
    }

    if (user.getStatus() == UserStatus.LOCKED) {
      auditLogService.log(
          user.getId(),
          user.getUsername(),
          AuditLogAction.LOGIN,
          AuditLogEntity.USER,
          user.getId().toString(),
          AuditLogStatus.FAILED,
          "Đăng nhập thất bại do tài khoản bị khóa");
      throw new AppException(HttpStatus.FORBIDDEN, AuthMessage.LOCKED.getMessage());
    }

    if (user.getStatus() == UserStatus.DELETED) {
      auditLogService.log(
          user.getId(),
          user.getUsername(),
          AuditLogAction.LOGIN,
          AuditLogEntity.USER,
          user.getId().toString(),
          AuditLogStatus.FAILED,
          "Đăng nhập thất bại do tài khoản đã bị xóa");
      throw new AppException(HttpStatus.FORBIDDEN, AuthMessage.DELETED.getMessage());
    }
  }

  private AppException invalidCredentialsException(String username) {
    auditLogService.log(
        null,
        username,
        AuditLogAction.LOGIN,
        AuditLogEntity.AUTH,
        null,
        AuditLogStatus.FAILED,
        "Đăng nhập thất bại do username không tồn tại");
    return new AppException(HttpStatus.UNAUTHORIZED, AuthMessage.INVALID_CREDENTIALS.getMessage());
  }

  private AppException invalidResetPasswordTokenException() {
    auditLogService.log(
        null,
        "anonymous",
        AuditLogAction.RESET_PASSWORD,
        AuditLogEntity.AUTH,
        null,
        AuditLogStatus.FAILED,
        "Đặt lại mật khẩu thất bại do token không hợp lệ");
    return new AppException(
        HttpStatus.UNAUTHORIZED, AuthMessage.INVALID_RESET_PASSWORD_TOKEN.getMessage());
  }
}
