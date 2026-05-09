package org.app.backend.modules.notification.dto;

import java.time.Instant;
import java.util.UUID;

import org.app.backend.modules.notification.enums.NotificationReadStatus;
import org.app.backend.modules.notification.enums.NotificationType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationFilterDTO {
  UUID userId;
  NotificationType type;
  NotificationReadStatus readStatus;
  Instant startDate;
  Instant endDate;
}
