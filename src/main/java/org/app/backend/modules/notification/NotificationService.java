package org.app.backend.modules.notification;

import java.util.List;
import java.util.UUID;
import org.app.backend.modules.auth.security.CustomUserDetails;
import org.app.backend.modules.notification.dto.*;
import org.app.backend.modules.notification.enums.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NotificationService {
  Page<NotificationDTO> findAllMyNotifications(
      CustomUserDetails user, NotificationFilterDTO filter, Pageable pageable);

  NotificationDTO findById(UUID id, CustomUserDetails user);

  long getUnreadCount(CustomUserDetails user);

  NotificationStatsDTO getStats(CustomUserDetails user);

  void markAsRead(UUID id, CustomUserDetails user);

  void markAllAsRead(CustomUserDetails user);

  void deleteNotification(UUID id, CustomUserDetails user);

  // Method for Scheduler (package-private)
  NotificationDTO createReminderNotification(
      UUID userId,
      NotificationType type,
      String title,
      String message,
      UUID relatedEntityId,
      String relatedEntityType);

  // Admin/Manager tạo thông báo thủ công
  NotificationDTO create(NotificationCreateDTO dto, CustomUserDetails actor);

  // Admin/Manager tạo thông báo bulk cho list user IDs
  List<NotificationDTO> createBulk(
      List<UUID> userIds,
      String title,
      String message,
      NotificationType type,
      UUID relatedEntityId,
      String relatedEntityType,
      CustomUserDetails actor);

  // Admin/Manager xem tất cả thông báo trong hệ thống (không lọc theo user)
  Page<NotificationDTO> findAllAllNotifications(CustomUserDetails actor, Pageable pageable);

  // Admin/Manager xem tất cả thông báo của một user cụ thể
  Page<NotificationDTO> findAllByUserId(UUID userId, CustomUserDetails actor, Pageable pageable);
}
