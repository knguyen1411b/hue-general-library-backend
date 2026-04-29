package org.app.backend.modules.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.app.backend.common.dto.*;
import org.app.backend.common.swagger.*;
import org.app.backend.common.swagger.BadRequestApiResponse;
import org.app.backend.modules.auth.security.CustomUserDetails;
import org.app.backend.modules.user.dto.*;
import org.app.backend.modules.user.UserRole;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "Người dùng (V1)", description = "Các API dùng để quản lý người dùng")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserV1Controller {
  UserService userService;

  @Operation(
      summary = "Lấy thông tin người dùng hiện tại",
      responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = DataApiResponseUserDTO.class)))
      })
  @UnauthorizedApiResponse
  @ForbiddenApiResponse
  @NotFoundApiResponse
  @GetMapping("/me")
  public DataApiResponse<UserDTO> getMe(@AuthenticationPrincipal CustomUserDetails user) {
    return DataApiResponse.success(
        userService.getMe(user), UserMessage.GET_ME_SUCCESS.getMessage());
  }

  @Operation(
      summary = "Cập nhật thông tin người dùng hiện tại",
      requestBody =
          @RequestBody(
              required = true,
              description = "Dữ liệu cập nhật người dùng hiện tại dưới dạng multipart/form-data",
              content =
                  @Content(
                      mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                      schema = @Schema(implementation = MeUpdateDTO.class))),
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
  @PatchMapping(value = "/me", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ApiResponse updateMe(
      @AuthenticationPrincipal CustomUserDetails user, @Valid @ModelAttribute MeUpdateDTO dto) {
    userService.updateMe(user, dto);
    return ApiResponse.success(UserMessage.UPDATE_ME_SUCCESS.getMessage());
  }

  @Operation(
      summary = "Lấy danh sách người dùng có phân trang",
      responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = PagedApiResponseUserDTO.class)))
      })
  @BadRequestApiResponse
  @UnauthorizedApiResponse
  @ForbiddenApiResponse
  @GetMapping
  public PagedApiResponse<UserDTO> index(
      @ParameterObject UserFilterDTO filter, @ParameterObject Pageable pageable) {
    return PagedApiResponse.success(
        userService.findAll(filter, pageable), UserMessage.INDEX_SUCCESS.getMessage());
  }

  @Operation(
      summary = "Lấy chi tiết người dùng theo ID",
      parameters = {@Parameter(name = "id", description = "ID của người dùng", required = true)},
      responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = DataApiResponseUserDTO.class)))
      })
  @NotFoundApiResponse
  @UnauthorizedApiResponse
  @ForbiddenApiResponse
  @GetMapping("/{id}")
  public DataApiResponse<UserDTO> show(@PathVariable UUID id) {
    return DataApiResponse.success(userService.findById(id), UserMessage.SHOW_SUCCESS.getMessage());
  }

  @Operation(
      summary = "Tạo mới người dùng",
      requestBody =
          @RequestBody(
              required = true,
              content =
                  @Content(
                      mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                      schema = @Schema(implementation = UserCreateDTO.class))),
      responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponse.class)))
      })
  @UnauthorizedApiResponse
  @ForbiddenApiResponse
  @BadRequestApiResponse
  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
  public ApiResponse create(
      @Valid @ModelAttribute UserCreateDTO dto, @AuthenticationPrincipal CustomUserDetails actor) {

    // Forbid creating ADMIN role via API
    if (dto.getRole() == UserRole.ADMIN) {
      return ApiResponse.forbidden("Cannot create ADMIN role via API");
    }

    // MANAGER can only create USER role
    if (actor.getRole() == UserRole.MANAGER && dto.getRole() != UserRole.USER) {
      return ApiResponse.forbidden("MANAGER can only create USER role accounts");
    }

    userService.create(dto, actor);
    return ApiResponse.created(UserMessage.CREATE_SUCCESS.getMessage());
  }

  @Operation(
      summary = "Cập nhật người dùng theo ID",
      parameters = {
        @Parameter(name = "id", description = "ID của người dùng cần cập nhật", required = true)
      },
      requestBody =
          @RequestBody(
              required = true,
              description = "Dữ liệu cập nhật người dùng dưới dạng multipart/form-data",
              content =
                  @Content(
                      mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                      schema = @Schema(implementation = UserUpdateDTO.class))),
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
  @BadRequestApiResponse
  @NotFoundApiResponse
  @PatchMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
  public ApiResponse update(
      @PathVariable UUID id,
      @Valid @ModelAttribute UserUpdateDTO dto,
      @AuthenticationPrincipal CustomUserDetails actor) {

    // Check if actor is MANAGER: can only update USER role users, cannot change roles
    if (actor.getRole() == UserRole.MANAGER) {
      UserDTO targetUser = userService.findById(id);
      if (targetUser.getRole() != UserRole.USER) {
        return ApiResponse.forbidden("MANAGER can only update USER role accounts");
      }
      if (dto.getRole() != null && dto.getRole() != UserRole.USER) {
        return ApiResponse.forbidden("MANAGER cannot assign MANAGER/ADMIN roles");
      }
    }

    // ADMIN cannot assign ADMIN role via API
    if (dto.getRole() == UserRole.ADMIN) {
      return ApiResponse.forbidden("Cannot assign ADMIN role via API");
    }

    userService.update(id, dto, actor);
    return ApiResponse.success(UserMessage.UPDATE_SUCCESS.getMessage());
  }

  @Operation(
      summary = "Xóa người dùng theo ID",
      description = "Xóa người dùng dựa trên ID.",
      parameters = {
        @Parameter(name = "id", description = "ID của người dùng cần xóa", required = true)
      },
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
      @PathVariable UUID id, @AuthenticationPrincipal CustomUserDetails actor) {
    userService.delete(id, actor);
    return ApiResponse.success(UserMessage.DELETE_SUCCESS.getMessage());
  }

  public static class PagedApiResponseUserDTO extends PagedApiResponse<UserDTO> {}

  public static class DataApiResponseUserDTO extends DataApiResponse<UserDTO> {}
}
