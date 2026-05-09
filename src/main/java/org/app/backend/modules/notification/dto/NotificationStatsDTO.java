package org.app.backend.modules.notification.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationStatsDTO {
  long totalCount;
  long unreadCount;
  long readCount;
}