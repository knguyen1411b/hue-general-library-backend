package org.app.backend.modules.notification.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.app.backend.modules.notification.enums.NotificationType;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO để tạo thông báo bulk cho nhiều user")
public class NotificationBulkDTO {
  @NotEmpty(message = "Danh sách user IDs không được rỗng")
  @Schema(description = "Danh sách ID của các user nhận thông báo", 
          example = "[\"550e8400-e29b-41d4-a716-446655440000\", \"550e8400-e29b-41d4-a716-446655440001\"]")
  List<UUID> userIds;

  @NotBlank(message = "Tiêu đề không được rỗng")
  @Schema(description = "Tiêu đề thông báo", example = "Thông báo hệ thống")
  String title;

  @NotBlank(message = "Nội dung không được rỗng")
  @Schema(description = "Nội dung thông báo", example = "Bạn có thông báo mới từ hệ thống")
  String message;

  @NotNull(message = "Loại thông báo không được null")
  @Schema(description = "Loại thông báo", example = "RENTAL_DUE_REMINDER")
  NotificationType type;

  @Schema(description = "ID thực thể liên quan (có thể null)", 
          example = "550e8400-e29b-41d4-a716-446655440002")
  UUID relatedEntityId;

  @Schema(description = "Kiểu thực thể liên quan (có thể null)", example = "BOOK")
  String relatedEntityType;
}