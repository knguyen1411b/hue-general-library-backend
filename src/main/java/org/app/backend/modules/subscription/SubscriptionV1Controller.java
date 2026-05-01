package org.app.backend.modules.subscription;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.app.backend.common.dto.ApiResponse;
import org.app.backend.common.dto.DataApiResponse;
import org.app.backend.common.dto.PagedApiResponse;
import org.app.backend.common.swagger.BadRequestApiResponse;
import org.app.backend.common.swagger.ForbiddenApiResponse;
import org.app.backend.common.swagger.NotFoundApiResponse;
import org.app.backend.common.swagger.UnauthorizedApiResponse;
import org.app.backend.modules.auth.security.CustomUserDetails;
import org.app.backend.modules.subscription.dto.SubscriptionCreateDTO;
import org.app.backend.modules.subscription.dto.SubscriptionDTO;
import org.app.backend.modules.subscription.dto.SubscriptionUpdateDTO;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/subscriptions")
@RequiredArgsConstructor
@Tag(
    name = "Gói cước",
    description = "Các API dùng để quản lý các gói cước (subscription) trong thư viện"
)
public class SubscriptionV1Controller {

    private final SubscriptionService subscriptionService;

    @Operation(
        summary = "Lấy danh sách gói đăng ký có phân trang",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                        implementation = PagedApiResponseSubscriptionDTO.class
                    )
                )
            ),
        }
    )
    @BadRequestApiResponse
    @UnauthorizedApiResponse
    @ForbiddenApiResponse
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('LIBRARIAN')")
    public PagedApiResponse<SubscriptionDTO> index(
        @ParameterObject Pageable pageable
    ) {
        return PagedApiResponse.success(
            subscriptionService.findAll(pageable),
            SubscriptionMessage.INDEX_SUCCESS.getMessage()
        );
    }

    @Operation(
        summary = "Lấy chi tiết gói đăng ký theo ID",
        parameters = {
            @Parameter(
                name = "id",
                description = "ID của gói đăng ký",
                required = true
            ),
        },
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                        implementation = DataApiResponseSubscriptionDTO.class
                    )
                )
            ),
        }
    )
    @NotFoundApiResponse
    @UnauthorizedApiResponse
    @ForbiddenApiResponse
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('LIBRARIAN') or hasRole('USER')")
    public DataApiResponse<SubscriptionDTO> show(@PathVariable UUID id) {
        return DataApiResponse.success(
            subscriptionService.findById(id),
            SubscriptionMessage.SHOW_SUCCESS.getMessage()
        );
    }

    @Operation(
        summary = "Lấy chi tiết gói đăng ký theo key",
        parameters = {
            @Parameter(
                name = "key",
                description = "Key của gói đăng ký",
                required = true
            ),
        },
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                        implementation = DataApiResponseSubscriptionDTO.class
                    )
                )
            ),
        }
    )
    @NotFoundApiResponse
    @UnauthorizedApiResponse
    @ForbiddenApiResponse
    @GetMapping("/key/{key}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('LIBRARIAN') or hasRole('USER')")
    public DataApiResponse<SubscriptionDTO> findByKey(
        @PathVariable String key
    ) {
        return DataApiResponse.success(
            subscriptionService.findByKey(key),
            SubscriptionMessage.SHOW_SUCCESS.getMessage()
        );
    }

    @Operation(
        summary = "Tạo mới gói đăng ký",
        requestBody = @RequestBody(
            required = true,
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = SubscriptionCreateDTO.class)
            )
        ),
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "201",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponse.class)
                )
            ),
        }
    )
    @UnauthorizedApiResponse
    @ForbiddenApiResponse
    @BadRequestApiResponse
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse create(
        @Valid @RequestBody SubscriptionCreateDTO dto,
        @AuthenticationPrincipal CustomUserDetails actor
    ) {
        subscriptionService.create(dto, actor);
        return ApiResponse.created(
            SubscriptionMessage.CREATE_SUCCESS.getMessage()
        );
    }

    @Operation(
        summary = "Cập nhật gói đăng ký theo ID",
        parameters = {
            @Parameter(
                name = "id",
                description = "ID của gói đăng ký cần cập nhật",
                required = true
            ),
        },
        requestBody = @RequestBody(
            required = true,
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = SubscriptionUpdateDTO.class)
            )
        ),
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponse.class)
                )
            ),
        }
    )
    @UnauthorizedApiResponse
    @ForbiddenApiResponse
    @BadRequestApiResponse
    @NotFoundApiResponse
    @PatchMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse update(
        @PathVariable UUID id,
        @Valid @RequestBody SubscriptionUpdateDTO dto,
        @AuthenticationPrincipal CustomUserDetails actor
    ) {
        subscriptionService.update(id, dto, actor);
        return ApiResponse.success(
            SubscriptionMessage.UPDATE_SUCCESS.getMessage()
        );
    }

    @Operation(
        summary = "Xóa gói đăng ký theo ID",
        description = "Xóa gói đăng ký dựa trên ID.",
        parameters = {
            @Parameter(
                name = "id",
                description = "ID của gói đăng ký cần xóa",
                required = true
            ),
        },
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponse.class)
                )
            ),
        }
    )
    @UnauthorizedApiResponse
    @ForbiddenApiResponse
    @NotFoundApiResponse
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse delete(
        @PathVariable UUID id,
        @AuthenticationPrincipal CustomUserDetails actor
    ) {
        subscriptionService.delete(id, actor);
        return ApiResponse.success(
            SubscriptionMessage.DELETE_SUCCESS.getMessage()
        );
    }

    public static class PagedApiResponseSubscriptionDTO
        extends PagedApiResponse<SubscriptionDTO> {}

    public static class DataApiResponseSubscriptionDTO
        extends DataApiResponse<SubscriptionDTO> {}
}
