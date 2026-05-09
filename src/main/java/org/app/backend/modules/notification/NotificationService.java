package org.app.backend.modules.notification;

import java.util.List;
import java.util.UUID;
import org.app.backend.modules.auth.security.CustomUserDetails;
import org.app.backend.modules.notification.dto.*;
import org.app.backend.modules.notification.enums.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NotificationService {
  Page<NotificationDTO> findAllMyNotifications(CustomUserDetails user, NotificationFilterDTO filter, Pageable pageable);

  NotificationDTO findById(UUID id, CustomUserDetails user);

  long getUnreadCount(CustomUserDetails user);

  NotificationStatsDTO getStats(CustomUserDetails user);

  void markAsRead(UUID id, CustomUserDetails user);

  void markAllAsRead(CustomUserDetails user);

  void deleteNotification(UUID id, CustomUserDetails user);

  // Method for Scheduler (package-private)
  NotificationDTO createReminderNotification(UUID userId, NotificationType type, String title, String message, UUID relatedEntityId, String relatedEntityType);
}