package org.app.backend.modules.notification;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.app.backend.common.exception.AppException;
import org.app.backend.modules.auth.security.CustomUserDetails;
import org.app.backend.modules.notification.dto.NotificationDTO;
import org.app.backend.modules.notification.enums.NotificationReadStatus;
import org.app.backend.modules.notification.enums.NotificationStatus;
import org.app.backend.modules.notification.enums.NotificationType;
import org.app.backend.modules.user.User;
import org.app.backend.modules.user.UserRepository;
import org.app.backend.modules.user.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

  @Mock private NotificationRepository notificationRepository;
  @Mock private UserRepository userRepository;
  @Mock private ModelMapper modelMapper;

  @InjectMocks private NotificationServiceImpl notificationService;

  private Notification mockNotification;
  private CustomUserDetails mockUser;
  private UUID notificationId;
  private UUID userId;

  @BeforeEach
  void setUp() {
    notificationId = UUID.randomUUID();
    userId = UUID.randomUUID();

    mockNotification = Notification.builder()
        .id(notificationId)
        .readStatus(NotificationReadStatus.UNREAD)
        .notificationStatus(NotificationStatus.PENDING)
        .build();

    mockUser = new CustomUserDetails();
    mockUser.setId(userId);
    mockUser.setUsername("testuser");
    mockUser.setRole(UserRole.ADMIN);
  }

  @Test
  @DisplayName("Find By Id - Success")
  void testFindById_Success() {
    NotificationDTO dto = new NotificationDTO();
    dto.setId(notificationId);

    when(notificationRepository.findByIdAndUser_Id(notificationId, userId))
        .thenReturn(Optional.of(mockNotification));
    when(modelMapper.map(mockNotification, NotificationDTO.class)).thenReturn(dto);

    NotificationDTO result = notificationService.findById(notificationId, mockUser);

    assertNotNull(result);
    assertEquals(notificationId, result.getId());
  }

  @Test
  @DisplayName("Find By Id - Not Found")
  void testFindById_NotFound() {
    when(notificationRepository.findByIdAndUser_Id(notificationId, userId))
        .thenReturn(Optional.empty());

    assertThrows(AppException.class, () -> notificationService.findById(notificationId, mockUser));
  }

  @Test
  @DisplayName("Mark As Read - Changes status to READ")
  void testMarkAsRead_Success() {
    when(notificationRepository.findByIdAndUser_Id(notificationId, userId))
        .thenReturn(Optional.of(mockNotification));

    notificationService.markAsRead(notificationId, mockUser);

    assertEquals(NotificationReadStatus.READ, mockNotification.getReadStatus());
    assertNotNull(mockNotification.getReadAt());
    verify(notificationRepository, times(1)).save(mockNotification);
  }

  @Test
  @DisplayName("Mark All As Read - Marks all unread notifications as read")
  void testMarkAllAsRead_Success() {
    Notification noti2 = Notification.builder()
        .readStatus(NotificationReadStatus.UNREAD)
        .build();

    when(notificationRepository.findByUser_IdAndReadStatus(userId, NotificationReadStatus.UNREAD))
        .thenReturn(List.of(mockNotification, noti2));

    notificationService.markAllAsRead(mockUser);

    assertEquals(NotificationReadStatus.READ, mockNotification.getReadStatus());
    assertEquals(NotificationReadStatus.READ, noti2.getReadStatus());
    verify(notificationRepository, times(1)).saveAll(anyList());
  }

  @Test
  @DisplayName("Get Unread Count - Returns correct count")
  void testGetUnreadCount() {
    when(notificationRepository.countByUser_IdAndReadStatus(userId, NotificationReadStatus.UNREAD))
        .thenReturn(5L);

    long count = notificationService.getUnreadCount(mockUser);

    assertEquals(5L, count);
  }

  @Test
  @DisplayName("Delete Notification - Success")
  void testDeleteNotification_Success() {
    when(notificationRepository.findByIdAndUser_Id(notificationId, userId))
        .thenReturn(Optional.of(mockNotification));

    notificationService.deleteNotification(notificationId, mockUser);

    verify(notificationRepository, times(1)).delete(mockNotification);
  }

  @Test
  @DisplayName("Create Reminder Notification - Success")
  void testCreateReminderNotification_Success() {
    User targetUser = new User();
    targetUser.setId(userId);

    NotificationDTO resultDto = new NotificationDTO();
    Notification savedNotification = Notification.builder().id(UUID.randomUUID()).build();

    when(userRepository.findById(userId)).thenReturn(Optional.of(targetUser));
    when(notificationRepository.save(any(Notification.class))).thenReturn(savedNotification);
    when(modelMapper.map(savedNotification, NotificationDTO.class)).thenReturn(resultDto);

    NotificationDTO result = notificationService.createReminderNotification(
        userId, NotificationType.RENTAL_DUE_REMINDER, "Title", "Message", UUID.randomUUID(), "RENTAL");

    assertNotNull(result);
    verify(notificationRepository, times(1)).save(any(Notification.class));
  }

  @Test
  @DisplayName("Create Bulk Notifications - Empty user list throws exception")
  void testCreateBulk_EmptyUserIds_ThrowsException() {
    assertThrows(AppException.class, () ->
        notificationService.createBulk(List.of(), "Title", "Message",
            NotificationType.RENTAL_DUE_REMINDER, UUID.randomUUID(), "RENTAL", mockUser));
  }
}
