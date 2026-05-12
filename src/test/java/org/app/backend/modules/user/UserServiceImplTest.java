package org.app.backend.modules.user;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.UUID;
import org.app.backend.common.exception.AppException;
import org.app.backend.core.file.FileService;
import org.app.backend.modules.audit.AuditLogService;
import org.app.backend.modules.audit.enums.AuditLogAction;
import org.app.backend.modules.audit.enums.AuditLogEntity;
import org.app.backend.modules.audit.enums.AuditLogStatus;
import org.app.backend.modules.auth.security.CustomUserDetails;
import org.app.backend.modules.librarycard.LibraryCardRepository;
import org.app.backend.modules.user.dto.UserCreateDTO;
import org.app.backend.modules.user.dto.UserDTO;
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
class UserServiceImplTest {

  @Mock private UserRepository userRepository;
  @Mock private PasswordEncoder passwordEncoder;
  @Mock private FileService fileService;
  @Mock private ModelMapper modelMapper;
  @Mock private AuditLogService auditLogService;
  @Mock private LibraryCardRepository libraryCardRepository;

  @InjectMocks private UserServiceImpl userService;

  private User mockUser;
  private CustomUserDetails mockUserDetails;
  private UUID userId;

  @BeforeEach
  void setUp() {
    userId = UUID.randomUUID();

    mockUser = new User();
    mockUser.setId(userId);
    mockUser.setUsername("testuser");
    mockUser.setEmail("test@test.com");
    mockUser.setStatus(UserStatus.ACTIVE);
    mockUser.setRole(UserRole.USER);

    mockUserDetails = new CustomUserDetails();
    mockUserDetails.setId(userId);
    mockUserDetails.setUsername("testuser");
  }

  @Test
  @DisplayName("Get Me - Success")
  void testGetMe() {
    UserDTO mockDto = new UserDTO();
    mockDto.setId(userId);
    mockDto.setUsername("testuser");

    when(modelMapper.map(mockUserDetails, UserDTO.class)).thenReturn(mockDto);

    UserDTO result = userService.getMe(mockUserDetails);

    assertNotNull(result);
    assertEquals("testuser", result.getUsername());
    assertEquals(userId, result.getId());
  }

  @Test
  @DisplayName("Find By Id - Success")
  void testFindById_Success() {
    UserDTO mockDto = new UserDTO();
    mockDto.setId(userId);
    mockDto.setUsername("testuser");

    when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
    when(modelMapper.map(mockUser, UserDTO.class)).thenReturn(mockDto);

    UserDTO result = userService.findById(userId);

    assertNotNull(result);
    assertEquals("testuser", result.getUsername());
  }

  @Test
  @DisplayName("Find By Id - Not Found")
  void testFindById_NotFound() {
    when(userRepository.findById(userId)).thenReturn(Optional.empty());

    AppException exception = assertThrows(AppException.class, () -> userService.findById(userId));
    assertEquals(UserMessage.NOT_FOUND.getMessage(), exception.getMessage());
  }

  @Test
  @DisplayName("Create User - Success")
  void testCreateUser_Success() {
    UserCreateDTO dto = new UserCreateDTO();
    dto.setUsername("newuser");
    dto.setEmail("newuser@test.com");
    dto.setPassword("password123");
    dto.setIdentityNumber("123456789");

    User mappedUser = new User();
    mappedUser.setId(UUID.randomUUID());
    mappedUser.setUsername("newuser");

    when(userRepository.existsByUsername(dto.getUsername())).thenReturn(false);
    when(userRepository.existsByEmail(dto.getEmail())).thenReturn(false);
    when(userRepository.existsByIdentityNumber(dto.getIdentityNumber())).thenReturn(false);

    when(modelMapper.map(dto, User.class)).thenReturn(mappedUser);
    when(passwordEncoder.encode(dto.getPassword())).thenReturn("encodedPassword");

    userService.create(dto, mockUserDetails);

    verify(userRepository, times(2)).save(mappedUser); // Saved twice in create()
    verify(auditLogService, times(1))
        .log(
            eq(mockUserDetails.getId()),
            eq(mockUserDetails.getUsername()),
            eq(AuditLogAction.CREATE),
            eq(AuditLogEntity.USER),
            anyString(),
            eq(AuditLogStatus.SUCCESS),
            anyString());
  }

  @Test
  @DisplayName("Create User - Username Taken")
  void testCreateUser_UsernameTaken() {
    UserCreateDTO dto = new UserCreateDTO();
    dto.setUsername("existinguser");

    when(userRepository.existsByUsername(dto.getUsername())).thenReturn(true);

    AppException exception =
        assertThrows(AppException.class, () -> userService.create(dto, mockUserDetails));
    assertEquals(UserMessage.USERNAME_TAKEN.getMessage(), exception.getMessage());
    verify(userRepository, never()).save(any());
  }

  @Test
  @DisplayName("Delete User - Success")
  void testDeleteUser_Success() {
    when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

    userService.delete(userId, mockUserDetails);

    assertEquals(UserStatus.DELETED, mockUser.getStatus());
    verify(userRepository, times(1)).save(mockUser);
    verify(auditLogService, times(1))
        .log(
            eq(mockUserDetails.getId()),
            eq(mockUserDetails.getUsername()),
            eq(AuditLogAction.DELETE),
            eq(AuditLogEntity.USER),
            eq(userId.toString()),
            eq(AuditLogStatus.SUCCESS),
            anyString());
  }
}
