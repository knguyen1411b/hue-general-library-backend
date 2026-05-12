package org.app.backend.modules.audit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;
import java.util.UUID;
import org.app.backend.common.exception.AppException;
import org.app.backend.modules.audit.dto.AuditLogDTO;
import org.app.backend.modules.audit.enums.AuditLogAction;
import org.app.backend.modules.audit.enums.AuditLogEntity;
import org.app.backend.modules.audit.enums.AuditLogStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

@ExtendWith(MockitoExtension.class)
class AuditLogServiceImplTest {

  @Mock private AuditLogRepository auditLogRepository;
  @Mock private HttpServletRequest request;
  @Mock private ModelMapper modelMapper;

  @InjectMocks private AuditLogServiceImpl auditLogService;

  private UUID auditId;
  private AuditLog mockAuditLog;

  @BeforeEach
  void setUp() {
    auditId = UUID.randomUUID();
    mockAuditLog =
        AuditLog.builder()
            .id(auditId)
            .userId(UUID.randomUUID())
            .username("testuser")
            .action(AuditLogAction.LOGIN)
            .entityName(AuditLogEntity.AUTH)
            .entityId(auditId.toString())
            .status(AuditLogStatus.SUCCESS)
            .message("Đăng nhập thành công")
            .ipAddress("127.0.0.1")
            .userAgent("Mozilla")
            .build();
  }

  @Test
  @DisplayName("Log action - Success with X-Forwarded-For")
  void testLogAction_WithXForwardedFor() {
    // Arrange
    UUID userId = UUID.randomUUID();
    String username = "testuser";

    when(request.getHeader("X-Forwarded-For")).thenReturn("192.168.1.1, 10.0.0.1");
    when(request.getHeader("User-Agent")).thenReturn("Chrome");

    // Act
    auditLogService.log(
        userId,
        username,
        AuditLogAction.LOGIN,
        AuditLogEntity.AUTH,
        "entity-id",
        AuditLogStatus.SUCCESS,
        "Success");

    // Assert
    ArgumentCaptor<AuditLog> captor = ArgumentCaptor.forClass(AuditLog.class);
    verify(auditLogRepository, times(1)).save(captor.capture());

    AuditLog savedLog = captor.getValue();
    assertEquals(userId, savedLog.getUserId());
    assertEquals("testuser", savedLog.getUsername());
    assertEquals(AuditLogAction.LOGIN, savedLog.getAction());
    assertEquals("192.168.1.1", savedLog.getIpAddress());
    assertEquals("Chrome", savedLog.getUserAgent());
  }

  @Test
  @DisplayName("Log action - Success with RemoteAddr")
  void testLogAction_WithRemoteAddr() {
    // Arrange
    when(request.getHeader("X-Forwarded-For")).thenReturn(null);
    when(request.getRemoteAddr()).thenReturn("127.0.0.1");
    when(request.getHeader("User-Agent")).thenReturn("Firefox");

    // Act
    auditLogService.log(
        null,
        "anonymous",
        AuditLogAction.LOGIN,
        AuditLogEntity.AUTH,
        null,
        AuditLogStatus.FAILED,
        "Failed");

    // Assert
    ArgumentCaptor<AuditLog> captor = ArgumentCaptor.forClass(AuditLog.class);
    verify(auditLogRepository, times(1)).save(captor.capture());

    AuditLog savedLog = captor.getValue();
    assertNull(savedLog.getUserId());
    assertEquals("anonymous", savedLog.getUsername());
    assertEquals("127.0.0.1", savedLog.getIpAddress());
    assertEquals("Firefox", savedLog.getUserAgent());
  }

  @Test
  @DisplayName("Find By Id - Success")
  void testFindById_Success() {
    // Arrange
    AuditLogDTO mockDto = new AuditLogDTO();
    mockDto.setId(auditId);
    mockDto.setUsername("testuser");

    when(auditLogRepository.findById(auditId)).thenReturn(Optional.of(mockAuditLog));
    when(modelMapper.map(mockAuditLog, AuditLogDTO.class)).thenReturn(mockDto);

    // Act
    AuditLogDTO result = auditLogService.findById(auditId);

    // Assert
    assertNotNull(result);
    assertEquals(auditId, result.getId());
    assertEquals("testuser", result.getUsername());
  }

  @Test
  @DisplayName("Find By Id - Not Found")
  void testFindById_NotFound() {
    // Arrange
    when(auditLogRepository.findById(auditId)).thenReturn(Optional.empty());

    // Act & Assert
    AppException exception =
        assertThrows(AppException.class, () -> auditLogService.findById(auditId));
    assertEquals(AuditLogMessage.NOT_FOUND.getMessage(), exception.getMessage());
  }
}
