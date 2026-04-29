package org.app.backend.modules.payment;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.app.backend.common.dto.ApiResponse;
import org.app.backend.common.dto.DataApiResponse;
import org.app.backend.common.dto.PagedApiResponse;
import org.app.backend.common.swagger.*;
import org.app.backend.modules.auth.security.CustomUserDetails;
import org.app.backend.modules.payment.dto.PaymentCreateDTO;
import org.app.backend.modules.payment.dto.PaymentDTO;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Tag(name = "Giao dịch thanh toán (V1)", description = "Quản lý giao dịch thanh toán")
public class PaymentV1Controller {
  private final PaymentService paymentService;

  @Operation(
      summary = "Lấy danh sách giao dịch có phân trang",
      responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = PagedApiResponsePaymentDTO.class)))
      })
  @UnauthorizedApiResponse
  @ForbiddenApiResponse
  @GetMapping
  @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
  public PagedApiResponse<PaymentDTO> index(
      @RequestParam(required = false) UUID userId,
      @RequestParam(required = false) PaymentType paymentType,
      @RequestParam(required = false) PaymentStatus paymentStatus,
      @ParameterObject Pageable pageable) {
    return PagedApiResponse.success(
        paymentService.findAll(pageable, userId, paymentType, paymentStatus),
        "Lấy danh sách thanh toán thành công");
  }

  @Operation(
      summary = "Xem chi tiết giao dịch",
      parameters = @Parameter(name = "id", required = true),
      responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = DataApiResponsePaymentDTO.class)))
      })
  @UnauthorizedApiResponse
  @ForbiddenApiResponse
  @NotFoundApiResponse
  @GetMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
  public DataApiResponse<PaymentDTO> show(@PathVariable UUID id) {
    return DataApiResponse.success(
        paymentService.findById(id), "Lấy chi tiết thanh toán thành công");
  }

  @Operation(
      summary = "Tạo giao dịch mới",
      requestBody =
          @io.swagger.v3.oas.annotations.parameters.RequestBody(
              required = true,
              content =
                  @Content(
                      mediaType = MediaType.APPLICATION_JSON_VALUE,
                      schema = @Schema(implementation = PaymentCreateDTO.class))),
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
  @PostMapping
  @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
  public ApiResponse create(
      @Valid @RequestBody PaymentCreateDTO dto, @AuthenticationPrincipal CustomUserDetails actor) {
    paymentService.create(dto, actor);
    return ApiResponse.created("Tạo giao dịch thanh toán thành công");
  }

  @Operation(
      summary = "Xác nhận thanh toán thành công",
      parameters = @Parameter(name = "id", required = true),
      responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = DataApiResponsePaymentDTO.class)))
      })
  @UnauthorizedApiResponse
  @ForbiddenApiResponse
  @NotFoundApiResponse
  @PutMapping("/{id}/confirm")
  @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
  public DataApiResponse<PaymentDTO> confirm(
      @PathVariable UUID id, @AuthenticationPrincipal CustomUserDetails actor) {
    return DataApiResponse.success(
        paymentService.confirm(id, actor), "Xác nhận thanh toán thành công");
  }

  @Operation(
      summary = "Xóa giao dịch",
      parameters = @Parameter(name = "id", required = true),
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
  @PreAuthorize("hasRole('ADMIN')")
  public ApiResponse delete(
      @PathVariable UUID id, @AuthenticationPrincipal CustomUserDetails actor) {
    paymentService.delete(id, actor);
    return ApiResponse.success("Xóa giao dịch thanh toán thành công");
  }

  public static class PagedApiResponsePaymentDTO extends PagedApiResponse<PaymentDTO> {}

  public static class DataApiResponsePaymentDTO extends DataApiResponse<PaymentDTO> {}
}
