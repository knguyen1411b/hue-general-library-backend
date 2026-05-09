package org.app.backend.modules.notification;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;
import org.app.backend.modules.notification.enums.NotificationType;

@Entity
@Table(
    name = "tbl_notification_template",
    indexes = {
      @Index(name = "idx_template_key", columnList = "template_key", unique = true)
    })
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationTemplate {

  @Id
  @GeneratedValue
  @UuidGenerator(style = UuidGenerator.Style.TIME)
  UUID id;

  @Column(name = "template_key", nullable = false, unique = true, length = 100)
  String templateKey;

  @Column(nullable = false, length = 255)
  String name;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 50)
  NotificationType type;

  @Column(name = "title_template", nullable = false, length = 500)
  String titleTemplate;

  @Column(name = "message_template", columnDefinition = "text", nullable = false)
  String messageTemplate;

  @Column(columnDefinition = "text")
  String variables; 

  @CreationTimestamp
  @Column(name = "created_at", updatable = false, nullable = false)
  Instant createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at", nullable = false)
  Instant updatedAt;
}