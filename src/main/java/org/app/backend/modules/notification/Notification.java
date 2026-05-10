package org.app.backend.modules.notification;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.app.backend.modules.notification.enums.NotificationReadStatus;
import org.app.backend.modules.notification.enums.NotificationStatus;
import org.app.backend.modules.notification.enums.NotificationType;
import org.app.backend.modules.user.User;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(
    name = "tbl_notification",
    indexes = {
      @Index(name = "idx_notification_user_id", columnList = "user_id"),
      @Index(name = "idx_notification_read_status", columnList = "read_status"),
      @Index(name = "idx_notification_created_at", columnList = "created_at")
    })
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Notification {

  @Id
  @GeneratedValue
  @UuidGenerator(style = UuidGenerator.Style.TIME)
  UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  User user;

  @Enumerated(EnumType.STRING)
  @Column(name = "notification_type", nullable = false, length = 20)
  NotificationType type;

  @Column(name = "title", length = 255)
  String title;

  @Column(columnDefinition = "text", nullable = false)
  String message;

  @Builder.Default
  @Enumerated(EnumType.STRING)
  @Column(name = "read_status", nullable = false, length = 20)
  NotificationReadStatus readStatus = NotificationReadStatus.UNREAD;

  @Builder.Default
  @Enumerated(EnumType.STRING)
  @Column(name = "notification_status", nullable = false, length = 20)
  NotificationStatus notificationStatus = NotificationStatus.PENDING;

  @Column(name = "related_entity_id")
  UUID relatedEntityId;

  @Column(name = "related_entity_type", length = 50)
  String relatedEntityType;

  @CreationTimestamp
  @Column(name = "created_at", updatable = false, nullable = false)
  Instant createdAt;

  @Column(name = "read_at")
  Instant readAt;
}
