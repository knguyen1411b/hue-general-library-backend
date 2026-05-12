package org.app.backend.modules.notification;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.app.backend.common.dto.ApiResponse;
import org.app.backend.common.dto.DataApiResponse;
import org.app.backend.common.dto.PagedApiResponse;
import org.app.backend.common.swagger.*;
import org.app.backend.modules.auth.security.CustomUserDetails;
import org.app.backend.modules.notification.dto.*;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Thông báo (V1)")
public class NotificationV1Controller {

  NotificationService notificationService;

  @Operation(
      summary = "Lấy danh sách thông báo của user hiện tại có phân trang",
      responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = PagedApiResponseNotificationDTO.class)))
      })
  @UnauthorizedApiResponse
  @ForbiddenApiResponse
  @GetMapping
  public PagedApiResponse<NotificationDTO> findAllMyNotifications(
      @AuthenticationPrincipal CustomUserDetails user,
      @ParameterObject NotificationFilterDTO filter,
      @ParameterObject Pageable pageable) {
    return PagedApiResponse.success(
        notificationService.findAllMyNotifications(user, filter, pageable),
        "Lấy danh sách thông báo thành công");
  }

  @Operation(
      summary = "Xem chi tiết thông báo theo ID",
      parameters = {@Parameter(name = "id", description = "ID thông báo", required = true)},
      responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = DataApiResponseNotificationDTO.class)))
      })
  @UnauthorizedApiResponse
  @ForbiddenApiResponse
  @NotFoundApiResponse
  @GetMapping("/{id}")
  public DataApiResponse<NotificationDTO> findById(
      @PathVariable UUID id, @AuthenticationPrincipal CustomUserDetails user) {
    return DataApiResponse.success(
        notificationService.findById(id, user), "Lấy chi tiết thông báo thành công");
  }

  @Operation(
      summary = "Lấy thống kê thông báo",
      responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = DataApiResponseNotificationStatsDTO.class)))
      })
  @UnauthorizedApiResponse
  @ForbiddenApiResponse
  @GetMapping("/stats")
  public DataApiResponse<NotificationStatsDTO> getStats(
      @AuthenticationPrincipal CustomUserDetails user) {
    return DataApiResponse.success(
        notificationService.getStats(user), "Lấy thống kê thông báo thành công");
  }

  @Operation(
      summary = "Đánh dấu thông báo đã đọc",
      parameters = {@Parameter(name = "id", description = "ID thông báo", required = true)},
      responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponse.class)))
      })
  @UnauthorizedApiResponse
  @ForbiddenApiResponse
  @NotFoundApiResponse
  @PutMapping("/{id}/read")
  public ApiResponse markAsRead(
      @PathVariable UUID id, @AuthenticationPrincipal CustomUserDetails user) {
    notificationService.markAsRead(id, user);
    return ApiResponse.success("Đánh dấu đã đọc thành công");
  }

  @Operation(
      summary = "Đánh dấu tất cả thông báo đã đọc",
      responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponse.class)))
      })
  @UnauthorizedApiResponse
  @ForbiddenApiResponse
  @PutMapping("/read-all")
  public ApiResponse markAllAsRead(@AuthenticationPrincipal CustomUserDetails user) {
    notificationService.markAllAsRead(user);
    return ApiResponse.success("Đánh dấu tất cả đã đọc thành công");
  }

  @Operation(
      summary = "Tạo thông báo mới (Admin/Manager)",
      requestBody =
          @io.swagger.v3.oas.annotations.parameters.RequestBody(
              required = true,
              content =
                  @Content(
                      mediaType = MediaType.APPLICATION_JSON_VALUE,
                      schema = @Schema(implementation = NotificationCreateDTO.class))),
      responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = DataApiResponseNotificationDTO.class)))
      })
  @UnauthorizedApiResponse
  @ForbiddenApiResponse
  @BadRequestApiResponse
  @PostMapping
  @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
  public DataApiResponse<NotificationDTO> create(
      @Valid @RequestBody NotificationCreateDTO dto,
      @AuthenticationPrincipal CustomUserDetails actor) {
    return DataApiResponse.success(
        notificationService.create(dto, actor), "Tạo thông báo thành công");
  }

  @Operation(
      summary = "Tạo thông báo bulk cho nhiều người dùng (Admin/Manager)",
      requestBody =
          @io.swagger.v3.oas.annotations.parameters.RequestBody(
              required = true,
              content =
                  @Content(
                      mediaType = MediaType.APPLICATION_JSON_VALUE,
                      schema = @Schema(implementation = NotificationBulkDTO.class))),
      responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = DataApiResponseListNotificationDTO.class)))
      })
  @UnauthorizedApiResponse
  @ForbiddenApiResponse
  @BadRequestApiResponse
  @PostMapping("/bulk")
  @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
  public DataApiResponse<List<NotificationDTO>> createBulk(
      @Valid @RequestBody NotificationBulkDTO dto,
      @AuthenticationPrincipal CustomUserDetails actor) {
    List<NotificationDTO> notifications =
        notificationService.createBulk(
            dto.getUserIds(),
            dto.getTitle(),
            dto.getMessage(),
            dto.getType(),
            dto.getRelatedEntityId(),
            dto.getRelatedEntityType(),
            actor);
    return DataApiResponse.success(notifications, "Tạo thông báo bulk thành công");
  }

  @Operation(
      summary = "Xóa thông báo",
      parameters = {@Parameter(name = "id", description = "ID thông báo", required = true)},
      responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponse.class)))
      })
  @UnauthorizedApiResponse
  @ForbiddenApiResponse
  @NotFoundApiResponse
  @DeleteMapping("/{id}")
  public ApiResponse delete(
      @PathVariable UUID id, @AuthenticationPrincipal CustomUserDetails user) {
    notificationService.deleteNotification(id, user);
    return ApiResponse.success("Xóa thông báo thành công");
  }

  @Operation(
      summary = "Admin/Manager: Lấy tất cả thông báo trong hệ thống (có phân trang)",
      responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = PagedApiResponseNotificationDTO.class)))
      })
  @UnauthorizedApiResponse
  @ForbiddenApiResponse
  @GetMapping("/admin/all")
  @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
  public PagedApiResponse<NotificationDTO> findAllAllNotifications(
      @AuthenticationPrincipal CustomUserDetails actor, @ParameterObject Pageable pageable) {
    return PagedApiResponse.success(
        notificationService.findAllAllNotifications(actor, pageable),
        "Lấy tất cả thông báo thành công");
  }

  @Operation(
      summary = "Admin/Manager: Lấy tất cả thông báo của một user cụ thể (có phân trang)",
      parameters = {
        @Parameter(
            name = "userId",
            description = "ID người dùng cần xem thông báo",
            required = true)
      },
      responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = PagedApiResponseNotificationDTO.class)))
      })
  @UnauthorizedApiResponse
  @ForbiddenApiResponse
  @GetMapping("/admin/user/{userId}")
  @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
  public PagedApiResponse<NotificationDTO> findAllByUserId(
      @PathVariable UUID userId,
      @AuthenticationPrincipal CustomUserDetails actor,
      @ParameterObject Pageable pageable) {
    return PagedApiResponse.success(
        notificationService.findAllByUserId(userId, actor, pageable),
        "Lấy thông báo của user thành công");
  }

  public static class PagedApiResponseNotificationDTO extends PagedApiResponse<NotificationDTO> {}

  public static class DataApiResponseNotificationDTO extends DataApiResponse<NotificationDTO> {}

  public static class DataApiResponseNotificationStatsDTO
      extends DataApiResponse<NotificationStatsDTO> {}

  public static class DataApiResponseListNotificationDTO
      extends DataApiResponse<List<NotificationDTO>> {}
}
