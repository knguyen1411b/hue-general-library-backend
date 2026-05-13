package org.app.backend.modules.usersubscription;

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
import org.app.backend.common.swagger.ForbiddenApiResponse;
import org.app.backend.common.swagger.NotFoundApiResponse;
import org.app.backend.common.swagger.UnauthorizedApiResponse;
import org.app.backend.modules.auth.security.CustomUserDetails;
import org.app.backend.modules.usersubscription.dto.UserSubscriptionCreateDTO;
import org.app.backend.modules.usersubscription.dto.UserSubscriptionFilterDTO;
import org.app.backend.modules.usersubscription.dto.UserSubscriptionResponseDTO;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user-subscriptions")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Quản lý đăng ký gói cước (V1)", description = "User Subscription")
public class UserSubscriptionV1Controller {

  UserSubscriptionService userSubscriptionService;

  @Operation(
      summary = "Lấy danh sách đăng ký gói cước",
      description = "Lấy danh sách tất cả các đăng ký gói cước.",
      responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = DataApiResponseUserSubscriptionList.class))),
      })
  @UnauthorizedApiResponse
  @ForbiddenApiResponse
  @GetMapping
  @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('USER')")
  public DataApiResponse<List<UserSubscriptionResponseDTO>> index(
      @ParameterObject UserSubscriptionFilterDTO filter,
      @AuthenticationPrincipal CustomUserDetails actor) {
    return DataApiResponse.success(
        userSubscriptionService.getAll(filter, actor), UserSubscriptionMessage.LIST_SUCCESS.getMessage());
  }

  @Operation(
      summary = "Lấy chi tiết đăng ký gói cước theo ID",
      description = "Lấy thông tin chi tiết của một đăng ký gói cước dựa trên ID.",
      parameters = {
        @Parameter(name = "id", description = "ID của đăng ký gói cước", required = true),
      },
      responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = DataApiResponseUserSubscription.class))),
      })
  @NotFoundApiResponse
  @UnauthorizedApiResponse
  @ForbiddenApiResponse
  @GetMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or #id == principal.id")
  public DataApiResponse<UserSubscriptionResponseDTO> show(
      @PathVariable UUID id,
      @AuthenticationPrincipal CustomUserDetails actor) {
    return DataApiResponse.success(
        userSubscriptionService.getById(id, actor), UserSubscriptionMessage.FOUND_SUCCESS.getMessage());
  }

  @Operation(
      summary = "Tạo mới đăng ký gói cước",
      description = "Tạo mới một đăng ký gói cước cho người dùng.",
      requestBody =
          @io.swagger.v3.oas.annotations.parameters.RequestBody(
              required = true,
              content =
                  @Content(
                      mediaType = MediaType.APPLICATION_JSON_VALUE,
                      schema = @Schema(implementation = UserSubscriptionCreateDTO.class))),
      responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponse.class))),
      })
  @UnauthorizedApiResponse
  @ForbiddenApiResponse
  @io.swagger.v3.oas.annotations.responses.ApiResponse(
      responseCode = "400",
      description = "Bad Request")
  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
  public ApiResponse create(
      @Valid @RequestBody UserSubscriptionCreateDTO dto,
      @AuthenticationPrincipal CustomUserDetails actor) {
    userSubscriptionService.create(dto);
    return ApiResponse.created(UserSubscriptionMessage.CREATED_SUCCESS.getMessage());
  }

  @Operation(
      summary = "Cập nhật đăng ký gói cước (Renew / Cancel)",
      description = "Cập nhật trạng thái đăng ký gói cước: RENEW gia hạn hoặc CANCEL hủy gói.",
      parameters = {
        @Parameter(
            name = "id",
            description = "ID của đăng ký gói cước cần cập nhật",
            required = true),
      },
      requestBody =
          @io.swagger.v3.oas.annotations.parameters.RequestBody(
              required = true,
              content =
                  @Content(
                      mediaType = MediaType.APPLICATION_JSON_VALUE,
                      schema = @Schema(implementation = UserSubscriptionAction.class))),
      responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponse.class))),
      })
  @UnauthorizedApiResponse
  @ForbiddenApiResponse
  @io.swagger.v3.oas.annotations.responses.ApiResponse(
      responseCode = "400",
      description = "Bad Request")
  @NotFoundApiResponse
  @PatchMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('USER')")
  public ApiResponse update(
      @PathVariable UUID id,
      @RequestBody UserSubscriptionAction action,
      @AuthenticationPrincipal CustomUserDetails actor) {
    userSubscriptionService.update(id, action, actor);
    return ApiResponse.success(UserSubscriptionMessage.UPDATED_SUCCESS.getMessage());
  }

  @Operation(
      summary = "Xóa đăng ký gói cước",
      description = "Xóa một đăng ký gói cước dựa trên ID.",
      parameters = {
        @Parameter(name = "id", description = "ID của đăng ký gói cước cần xóa", required = true),
      },
      responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponse.class))),
      })
  @UnauthorizedApiResponse
  @ForbiddenApiResponse
  @NotFoundApiResponse
  @DeleteMapping("/{id}")
  public ApiResponse delete(
      @PathVariable UUID id, @AuthenticationPrincipal CustomUserDetails actor) {
    userSubscriptionService.delete(id);
    return ApiResponse.success(UserSubscriptionMessage.DELETED_SUCCESS.getMessage());
  }

  public static class DataApiResponseUserSubscription
      extends DataApiResponse<UserSubscriptionResponseDTO> {}

  public static class DataApiResponseUserSubscriptionList
      extends DataApiResponse<List<UserSubscriptionResponseDTO>> {}
}