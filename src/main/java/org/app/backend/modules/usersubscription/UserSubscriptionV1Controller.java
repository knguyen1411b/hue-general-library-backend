package org.app.backend.modules.usersubscription;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
// import io.swagger.v3.oas.annotations.parameters.RequestBody;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.app.backend.common.dto.ApiResponse;
import org.app.backend.common.dto.DataApiResponse;
import org.app.backend.common.dto.PagedApiResponse;
import org.app.backend.common.swagger.BadRequestApiResponse;
import org.app.backend.common.swagger.ForbiddenApiResponse;
import org.app.backend.common.swagger.NotFoundApiResponse;
import org.app.backend.common.swagger.UnauthorizedApiResponse;
import org.app.backend.modules.auth.security.CustomUserDetails;
import org.app.backend.modules.usersubscription.dto.*;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user-subscriptions")
@RequiredArgsConstructor
@Tag(name = "Đăng ký gói cước", description = "Các API dùng để quản lý các đăng ký gói cước của người dùng (user subscription)")
public class UserSubscriptionV1Controller {

    private final UserSubscriptionService userSubscriptionService;

    @Operation(summary = "Lấy danh sách đăng ký người dùng có phân trang", responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PagedApiResponseUserSubscription.class))),
    })
    @BadRequestApiResponse
    @UnauthorizedApiResponse
    @ForbiddenApiResponse
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public PagedApiResponse<UserSubscription> index(
            @ParameterObject Pageable pageable) {
        return PagedApiResponse.success(
                userSubscriptionService.getAll(pageable),
                UserSubscriptionMessage.LIST_SUCCESS);
    }

    @Operation(summary = "Lấy chi tiết đăng ký người dùng theo ID", parameters = {
            @Parameter(name = "id", description = "ID của đăng ký người dùng", required = true),
    }, responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = DataApiResponseUserSubscription.class))),
    })
    @NotFoundApiResponse
    @UnauthorizedApiResponse
    @ForbiddenApiResponse
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or @securityService.isUserSubscriptionOwner(#id)")
    public DataApiResponse<UserSubscription> show(@PathVariable UUID id) {
        return DataApiResponse.success(
                userSubscriptionService.getById(id),
                UserSubscriptionMessage.FOUND_SUCCESS);
    }

    @Operation(summary = "Tạo mới đăng ký người dùng", requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = UserSubscription.class))), responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
    })
    @UnauthorizedApiResponse
    @ForbiddenApiResponse
    @BadRequestApiResponse
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse create(
            @Valid @RequestBody UserSubscription userSubscription,
            @AuthenticationPrincipal CustomUserDetails actor) {
        userSubscriptionService.create(userSubscription);
        return ApiResponse.created(UserSubscriptionMessage.CREATED_SUCCESS);
    }

    @Operation(summary = "Cập nhật đăng ký người dùng theo ID", parameters = {
            @Parameter(name = "id", description = "ID của đăng ký người dùng cần cập nhật", required = true),
    }, requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = UserSubscription.class))), responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
    })
    @UnauthorizedApiResponse
    @ForbiddenApiResponse
    @BadRequestApiResponse
    @NotFoundApiResponse
    @PatchMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ApiResponse update(
            @PathVariable UUID id,
            @Valid @RequestBody UserSubscription userSubscription,
            @AuthenticationPrincipal CustomUserDetails actor) {
        userSubscriptionService.update(id, userSubscription);
        return ApiResponse.success(UserSubscriptionMessage.UPDATED_SUCCESS);
    }

    @Operation(summary = "Lấy danh sách đăng ký theo userId", parameters = {
            @Parameter(name = "userId", description = "ID của người dùng", required = true)
    }, responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = DataApiResponseUserSubscriptionList.class)))
    })
    @UnauthorizedApiResponse
    @ForbiddenApiResponse
    @GetMapping("/user/{userId}")
    public DataApiResponse<List<UserSubscriptionResponseDTO>> getByUser(
            @PathVariable UUID userId) {
        return DataApiResponse.success(
                userSubscriptionService.getByUserId(userId),
                UserSubscriptionMessage.LIST_SUCCESS);
    }

    @Operation(summary = "Lấy danh sách đăng ký theo subscriptionId", parameters = {
            @Parameter(name = "subscriptionId", description = "ID của gói", required = true)
    }, responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = DataApiResponseUserSubscriptionList.class)))
    })
    @UnauthorizedApiResponse
    @ForbiddenApiResponse
    @GetMapping("/subscription/{subscriptionId}")
    public DataApiResponse<List<UserSubscriptionResponseDTO>> getBySubscription(
            @PathVariable UUID subscriptionId) {
        return DataApiResponse.success(
                userSubscriptionService.getBySubscriptionId(subscriptionId),
                UserSubscriptionMessage.LIST_SUCCESS);
    }

    @Operation(summary = "Lấy danh sách đăng ký theo trạng thái", parameters = {
            @Parameter(name = "status", description = "Trạng thái subscription", required = true)
    }, responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = DataApiResponseUserSubscriptionList.class)))
    })
    @UnauthorizedApiResponse
    @ForbiddenApiResponse
    @GetMapping("/status/{status}")
    public DataApiResponse<List<UserSubscriptionResponseDTO>> getByStatus(
            @PathVariable UserSubscriptionStatus status) {
        return DataApiResponse.success(
                userSubscriptionService.getByStatus(status),
                UserSubscriptionMessage.LIST_SUCCESS);
    }

    @Operation(summary = "Lấy danh sách đăng ký đang active của user", parameters = {
            @Parameter(name = "userId", description = "ID của người dùng", required = true)
    }, responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = DataApiResponseUserSubscriptionList.class)))
    })
    @UnauthorizedApiResponse
    @ForbiddenApiResponse
    @GetMapping("/user/{userId}/active")
    public DataApiResponse<List<UserSubscriptionResponseDTO>> getActiveByUser(
            @PathVariable UUID userId) {
        return DataApiResponse.success(
                userSubscriptionService.getActiveSubscriptionsByUser(userId),
                UserSubscriptionMessage.LIST_SUCCESS);
    }

    @Operation(summary = "Kích hoạt đăng ký")
    @UnauthorizedApiResponse
    @ForbiddenApiResponse
    @NotFoundApiResponse
    @PostMapping("/{id}/activate")
    public DataApiResponse<UserSubscriptionResponseDTO> activate(@PathVariable UUID id) {
        return DataApiResponse.success(
                userSubscriptionService.activateSubscription(id),
                UserSubscriptionMessage.UPDATED_SUCCESS);
    }

    @Operation(summary = "Hết hạn đăng ký")
    @UnauthorizedApiResponse
    @ForbiddenApiResponse
    @NotFoundApiResponse
    @PostMapping("/{id}/expire")
    public DataApiResponse<UserSubscriptionResponseDTO> expire(@PathVariable UUID id) {
        return DataApiResponse.success(
                userSubscriptionService.expireSubscription(id),
                UserSubscriptionMessage.UPDATED_SUCCESS);
    }

    @Operation(summary = "Hủy đăng ký")
    @UnauthorizedApiResponse
    @ForbiddenApiResponse
    @NotFoundApiResponse
    @PostMapping("/{id}/cancel")
    public DataApiResponse<UserSubscriptionResponseDTO> cancel(@PathVariable UUID id) {
        return DataApiResponse.success(
                userSubscriptionService.cancelSubscription(id),
                UserSubscriptionMessage.UPDATED_SUCCESS);
    }

    @Operation(summary = "Gia hạn đăng ký")
    @UnauthorizedApiResponse
    @ForbiddenApiResponse
    @NotFoundApiResponse
    @PostMapping("/{id}/renew")
    public DataApiResponse<UserSubscriptionResponseDTO> renew(
            @PathVariable UUID id,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
        return DataApiResponse.success(
                userSubscriptionService.renewSubscription(id, startDate, endDate),
                UserSubscriptionMessage.UPDATED_SUCCESS);
    }

    @Operation(summary = "Xóa đăng ký người dùng theo ID", description = "Xóa đăng ký người dùng dựa trên ID.", parameters = {
            @Parameter(name = "id", description = "ID của đăng ký người dùng cần xóa", required = true),
    }, responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
    })
    @UnauthorizedApiResponse
    @ForbiddenApiResponse
    @NotFoundApiResponse
    @DeleteMapping("/{id}")
    public ApiResponse delete(
            @PathVariable UUID id,
            @AuthenticationPrincipal CustomUserDetails actor) {
        userSubscriptionService.delete(id);
        return ApiResponse.success(UserSubscriptionMessage.DELETED_SUCCESS);
    }

    public static class PagedApiResponseUserSubscription
            extends PagedApiResponse<UserSubscription> {
    }

    public static class DataApiResponseUserSubscription
            extends DataApiResponse<UserSubscription> {
    }

    public static class DataApiResponseUserSubscriptionList
            extends DataApiResponse<List<UserSubscriptionResponseDTO>> {
    }
}
