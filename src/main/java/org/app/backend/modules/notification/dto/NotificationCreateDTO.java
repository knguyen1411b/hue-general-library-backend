package org.app.backend.modules.notification.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.app.backend.modules.notification.enums.NotificationType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationCreateDTO {
  @NotNull(message = "ID người dùng không được để trống")
  UUID userId;

  @NotNull(message = "Loại thông báo không được để trống")
  NotificationType type;

  @NotBlank(message = "Tiêu đề không được để trống")
  String title;

  @NotBlank(message = "Nội dung không được để trống")
  String message;

  UUID relatedEntityId;
  String relatedEntityType;
}