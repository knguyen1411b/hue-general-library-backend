package org.app.backend.modules.notification.dto;

import java.time.Instant;
import java.util.UUID;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.app.backend.modules.notification.enums.NotificationType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationDTO {
  UUID id;
  UUID userId;
  NotificationType type;
  String title;
  String message;
  String readStatus; // FE dùng trường này làm status để hiển thị
  UUID relatedEntityId;
  String relatedEntityType;
  Instant createdAt;
  Instant readAt;
}
