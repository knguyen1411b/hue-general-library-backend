package org.app.backend.modules.auth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.app.backend.common.dto.*;
import org.app.backend.common.swagger.*;
import org.app.backend.common.swagger.BadRequestApiResponse;
import org.app.backend.modules.auth.dto.*;
import org.app.backend.modules.auth.security.CustomUserDetails;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Xác thực (V1)", description = "Các API dùng để xác thực và phân quyền")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthV1Controller {

  AuthService authService;

  @Operation(
      summary = "Đăng nhập",
      requestBody =
          @io.swagger.v3.oas.annotations.parameters.RequestBody(
              required = true,
              content =
                  @Content(
                      mediaType = "application/json",
                      schema = @Schema(implementation = SignInDTO.class))),
      responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = DataApiResponseTokenDTO.class)))
      })
  @SecurityRequirements
  @BadRequestApiResponse
  @UnauthorizedApiResponse
  @PostMapping("/sign-in")
  public DataApiResponse<TokenDTO> signIn(@RequestBody @Valid SignInDTO dto) {
    return DataApiResponse.success(authService.signIn(dto), AuthMessage.LOGIN_SUCCESS.getMessage());
  }

  @Operation(
      summary = "Làm mới token",
      requestBody =
          @io.swagger.v3.oas.annotations.parameters.RequestBody(
              required = true,
              content =
                  @Content(
                      mediaType = "application/json",
                      schema = @Schema(implementation = RefreshTokenDTO.class))),
      responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = DataApiResponseTokenDTO.class)))
      })
  @SecurityRequirements
  @BadRequestApiResponse
  @UnauthorizedApiResponse
  @PostMapping("/refresh")
  public DataApiResponse<TokenDTO> refresh(@RequestBody @Valid RefreshTokenDTO dto) {
    return DataApiResponse.success(
        authService.refresh(dto), AuthMessage.TOKEN_REFRESH_SUCCESS.getMessage());
  }

  @Operation(
      summary = "Đăng ký tài khoản",
      requestBody =
          @io.swagger.v3.oas.annotations.parameters.RequestBody(
              required = true,
              content =
                  @Content(
                      mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                      schema = @Schema(implementation = SignUpDTO.class))),
      responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponse.class)))
      })
  @SecurityRequirements
  @BadRequestApiResponse
  @PostMapping(value = "/sign-up", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ApiResponse signUp(@Valid @ModelAttribute SignUpDTO dto) {
    authService.signUp(dto);
    return ApiResponse.success(AuthMessage.SIGNUP_SUCCESS.getMessage());
  }

  @Operation(
      summary = "Đăng xuất",
      responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Đăng xuất thành công",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponse.class)))
      })
  @UnauthorizedApiResponse
  @ForbiddenApiResponse
  @DeleteMapping("/sign-out")
  public ApiResponse signOut(@AuthenticationPrincipal CustomUserDetails user) {
    authService.signOut(user.getId());
    return ApiResponse.success(AuthMessage.LOGOUT_SUCCESS.getMessage());
  }

  @Operation(
      summary = "Đổi mật khẩu",
      requestBody =
          @io.swagger.v3.oas.annotations.parameters.RequestBody(
              required = true,
              content =
                  @Content(
                      mediaType = "application/json",
                      schema = @Schema(implementation = ChangePasswordDTO.class))),
      responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponse.class)))
      })
  @BadRequestApiResponse
  @UnauthorizedApiResponse
  @ForbiddenApiResponse
  @PostMapping("/change-password")
  public ApiResponse changePassword(
      @AuthenticationPrincipal CustomUserDetails user, @RequestBody @Valid ChangePasswordDTO dto) {
    authService.changePassword(user.getId(), dto);
    return ApiResponse.success(AuthMessage.PASSWORD_CHANGE_SUCCESS.getMessage());
  }

  @Operation(
      summary = "Gửi email quên mật khẩu",
      requestBody =
          @io.swagger.v3.oas.annotations.parameters.RequestBody(
              required = true,
              content =
                  @Content(
                      mediaType = "application/json",
                      schema = @Schema(implementation = ForgotPasswordDTO.class))),
      responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponse.class)))
      })
  @SecurityRequirements
  @BadRequestApiResponse
  @NotFoundApiResponse
  @PostMapping("/forgot-password")
  public ApiResponse forgotPassword(@RequestBody @Valid ForgotPasswordDTO dto) {
    authService.forgotPassword(dto);
    return ApiResponse.success(AuthMessage.SEND_RESET_PASSWORD_SUCCESS.getMessage());
  }

  @Operation(
      summary = "Đặt lại mật khẩu",
      requestBody =
          @io.swagger.v3.oas.annotations.parameters.RequestBody(
              required = true,
              content =
                  @Content(
                      mediaType = "application/json",
                      schema = @Schema(implementation = ResetPasswordDTO.class))),
      responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponse.class)))
      })
  @SecurityRequirements
  @BadRequestApiResponse
  @UnauthorizedApiResponse
  @PostMapping("/reset-password")
  public ApiResponse resetPassword(@RequestBody @Valid ResetPasswordDTO dto) {
    authService.resetPassword(dto);
    return ApiResponse.success(AuthMessage.RESET_PASSWORD_SUCCESS.getMessage());
  }

  public static class DataApiResponseTokenDTO extends DataApiResponse<TokenDTO> {}
}
