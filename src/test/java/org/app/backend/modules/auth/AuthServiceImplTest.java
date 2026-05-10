package org.app.backend.modules.auth;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.app.backend.common.constants.JwtProperties;
import org.app.backend.common.exception.AppException;
import org.app.backend.core.file.FileService;
import org.app.backend.core.mail.MailService;
import org.app.backend.modules.audit.AuditLogService;
import org.app.backend.modules.audit.enums.AuditLogAction;
import org.app.backend.modules.audit.enums.AuditLogEntity;
import org.app.backend.modules.audit.enums.AuditLogStatus;
import org.app.backend.modules.auth.dto.SignInDTO;
import org.app.backend.modules.auth.dto.TokenDTO;
import org.app.backend.modules.auth.security.JwtService;
import org.app.backend.modules.user.User;
import org.app.backend.modules.user.UserRepository;
import org.app.backend.modules.user.UserStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

  @Mock private JwtService jwtService;
  @Mock private JwtProperties jwtProperties;
  @Mock private UserRepository userRepository;
  @Mock private PasswordEncoder passwordEncoder;
  @Mock private MailService mailService;
  @Mock private FileService fileService;
  @Mock private ModelMapper modelMapper;
  @Mock private AuditLogService auditLogService;

  @InjectMocks private AuthServiceImpl authService;

  private User mockUser;

  @BeforeEach
  void setUp() {
    mockUser = new User();
    mockUser.setId(UUID.randomUUID());
    mockUser.setUsername("testuser");
    mockUser.setPasswordHash("hashedpassword");
    mockUser.setStatus(UserStatus.ACTIVE);
  }

  @Test
  @DisplayName("Sign in - Success")
  void testSignIn_Success() {
    // Arrange
    SignInDTO signInDTO = new SignInDTO();
    signInDTO.setUsername("testuser");
    signInDTO.setPassword("password123");

    when(userRepository.findByUsername(signInDTO.getUsername())).thenReturn(Optional.of(mockUser));
    when(passwordEncoder.matches(signInDTO.getPassword(), mockUser.getPasswordHash())).thenReturn(true);
    when(jwtService.generateAccessToken(mockUser.getId())).thenReturn("access-token");
    when(jwtProperties.getAccessTokenExpiration()).thenReturn(3600000L);
    when(jwtProperties.getRefreshTokenExpiration()).thenReturn(86400000L);

    // Act
    TokenDTO result = authService.signIn(signInDTO);

    // Assert
    assertNotNull(result);
    assertEquals("access-token", result.getAccessToken());
    assertNotNull(result.getRefreshToken());
    assertEquals(3600000L, result.getAccessTokenExpiration());
    assertEquals(86400000L, result.getRefreshTokenExpiration());

    verify(userRepository, times(1)).save(mockUser);
    verify(auditLogService, times(1)).log(
        eq(mockUser.getId()),
        eq(mockUser.getUsername()),
        eq(AuditLogAction.LOGIN),
        eq(AuditLogEntity.AUTH),
        anyString(),
        eq(AuditLogStatus.SUCCESS),
        anyString()
    );
  }

  @Test
  @DisplayName("Sign in - Invalid Password")
  void testSignIn_InvalidPassword() {
    // Arrange
    SignInDTO signInDTO = new SignInDTO();
    signInDTO.setUsername("testuser");
    signInDTO.setPassword("wrongpassword");

    when(userRepository.findByUsername(signInDTO.getUsername())).thenReturn(Optional.of(mockUser));
    when(passwordEncoder.matches(signInDTO.getPassword(), mockUser.getPasswordHash())).thenReturn(false);

    // Act & Assert
    AppException exception = assertThrows(AppException.class, () -> authService.signIn(signInDTO));
    assertEquals(AuthMessage.INVALID_CREDENTIALS.getMessage(), exception.getMessage());

    verify(auditLogService, times(1)).log(
        eq(mockUser.getId()),
        eq(mockUser.getUsername()),
        eq(AuditLogAction.LOGIN),
        eq(AuditLogEntity.AUTH),
        anyString(),
        eq(AuditLogStatus.FAILED),
        anyString()
    );
    verify(userRepository, never()).save(any(User.class));
  }

  @Test
  @DisplayName("Sign in - User Not Found")
  void testSignIn_UserNotFound() {
    // Arrange
    SignInDTO signInDTO = new SignInDTO();
    signInDTO.setUsername("unknownuser");
    signInDTO.setPassword("password");

    when(userRepository.findByUsername(signInDTO.getUsername())).thenReturn(Optional.empty());

    // Act & Assert
    AppException exception = assertThrows(AppException.class, () -> authService.signIn(signInDTO));
    assertEquals(AuthMessage.INVALID_CREDENTIALS.getMessage(), exception.getMessage());

    verify(auditLogService, times(1)).log(
        isNull(),
        eq(signInDTO.getUsername()),
        eq(AuditLogAction.LOGIN),
        eq(AuditLogEntity.AUTH),
        isNull(),
        eq(AuditLogStatus.FAILED),
        anyString()
    );
  }

  @Test
  @DisplayName("Sign in - User Locked")
  void testSignIn_UserLocked() {
    // Arrange
    mockUser.setStatus(UserStatus.LOCKED);
    SignInDTO signInDTO = new SignInDTO();
    signInDTO.setUsername("testuser");
    signInDTO.setPassword("password123");

    when(userRepository.findByUsername(signInDTO.getUsername())).thenReturn(Optional.of(mockUser));
    when(passwordEncoder.matches(signInDTO.getPassword(), mockUser.getPasswordHash())).thenReturn(true);

    // Act & Assert
    AppException exception = assertThrows(AppException.class, () -> authService.signIn(signInDTO));
    assertEquals(AuthMessage.LOCKED.getMessage(), exception.getMessage());

    verify(auditLogService, times(1)).log(
        eq(mockUser.getId()),
        eq(mockUser.getUsername()),
        eq(AuditLogAction.LOGIN),
        eq(AuditLogEntity.USER),
        anyString(),
        eq(AuditLogStatus.FAILED),
        anyString()
    );
  }

  @Test
  @DisplayName("Sign up - Success")
  void testSignUp_Success() {
    // Arrange
    org.app.backend.modules.auth.dto.SignUpDTO signUpDTO = new org.app.backend.modules.auth.dto.SignUpDTO();
    signUpDTO.setUsername("newuser");
    signUpDTO.setPassword("password123");
    signUpDTO.setEmail("newuser@test.com");
    signUpDTO.setPhone("0123456789");
    signUpDTO.setIdentityNumber("123456789");
    
    when(userRepository.existsByUsername(signUpDTO.getUsername())).thenReturn(false);
    when(userRepository.existsByEmail(signUpDTO.getEmail())).thenReturn(false);
    when(userRepository.existsByPhone(signUpDTO.getPhone())).thenReturn(false);
    when(userRepository.existsByIdentityNumber(signUpDTO.getIdentityNumber())).thenReturn(false);

    User mappedUser = new User();
    mappedUser.setId(UUID.randomUUID());
    mappedUser.setUsername("newuser");
    when(modelMapper.map(signUpDTO, User.class)).thenReturn(mappedUser);
    when(passwordEncoder.encode(signUpDTO.getPassword())).thenReturn("encodedPassword");

    // Act
    authService.signUp(signUpDTO);

    // Assert
    assertEquals("encodedPassword", mappedUser.getPasswordHash());
    verify(userRepository, times(1)).saveAndFlush(mappedUser);
    verify(userRepository, times(1)).save(mappedUser); // Called again after file uploads (even if null)

    verify(auditLogService, times(1)).log(
        eq(mappedUser.getId()),
        eq(mappedUser.getUsername()),
        eq(AuditLogAction.SIGN_UP),
        eq(AuditLogEntity.USER),
        anyString(),
        eq(AuditLogStatus.SUCCESS),
        anyString()
    );
  }

  @Test
  @DisplayName("Sign up - Username Taken")
  void testSignUp_UsernameTaken() {
    // Arrange
    org.app.backend.modules.auth.dto.SignUpDTO signUpDTO = new org.app.backend.modules.auth.dto.SignUpDTO();
    signUpDTO.setUsername("existinguser");

    when(userRepository.existsByUsername(signUpDTO.getUsername())).thenReturn(true);

    // Act & Assert
    AppException exception = assertThrows(AppException.class, () -> authService.signUp(signUpDTO));
    assertEquals(org.app.backend.modules.user.UserMessage.USERNAME_TAKEN.getMessage(), exception.getMessage());
    verify(userRepository, never()).save(any());
  }
}
