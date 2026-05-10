package org.app.backend.modules.notification.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.app.backend.modules.notification.enums.NotificationType;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "DTO để tạo thông báo mới cho một user")
public class NotificationCreateDTO {
  @NotNull(message = "ID người dùng không được để trống")
  @Schema(description = "ID của user nhận thông báo", 
          example = "550e8400-e29b-41d4-a716-446655440000")
  UUID userId;

  @NotNull(message = "Loại thông báo không được để trống")
  @Schema(description = "Loại thông báo", example = "SYSTEM")
  NotificationType type;

  @NotBlank(message = "Tiêu đề không được để trống")
  @Schema(description = "Tiêu đề thông báo", example = "Thông báo mới")
  String title;

  @NotBlank(message = "Nội dung không được để trống")
  @Schema(description = "Nội dung thông báo", example = "Bạn có một thông báo mới từ hệ thống")
  String message;

  @Schema(description = "ID thực thể liên quan (có thể null)", 
          example = "550e8400-e29b-41d4-a716-446655440002")
  UUID relatedEntityId;

  @Schema(description = "Kiểu thực thể liên quan (có thể null)", example = "BOOK")
  String relatedEntityType;
}
