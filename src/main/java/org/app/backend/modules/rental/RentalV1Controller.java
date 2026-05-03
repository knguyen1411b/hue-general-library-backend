package org.app.backend.modules.rental;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.app.backend.common.dto.ApiResponse;
import org.app.backend.common.dto.DataApiResponse;
import org.app.backend.common.dto.PagedApiResponse;
import org.app.backend.common.swagger.*;
import org.app.backend.modules.auth.security.CustomUserDetails;
import org.app.backend.modules.rental.dto.RentalCreateDTO;
import org.app.backend.modules.rental.dto.RentalDTO;
import org.app.backend.modules.rental.enums.RentalStatus;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/rentals")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Phiếu mượn sách (V1)")
public class RentalV1Controller {
  RentalService rentalService;

  @Operation(
      summary = "Lấy danh sách phiếu mượn có phân trang",
      responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = PagedApiResponseRentalDTO.class)))
      })
  @UnauthorizedApiResponse
  @ForbiddenApiResponse
  @GetMapping
  @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
  public PagedApiResponse<RentalDTO> index(
      @RequestParam(required = false) UUID userId,
      @RequestParam(required = false) RentalStatus status,
      @RequestParam(required = false) UUID bookItemId,
      @ParameterObject Pageable pageable) {
    return PagedApiResponse.success(
        rentalService.findAll(pageable, userId, status, bookItemId),
        "Lấy danh sách phiếu mượn thành công");
  }

  @Operation(
      summary = "Xem chi tiết phiếu mượn",
      parameters = @Parameter(name = "id", required = true),
      responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = DataApiResponseRentalDTO.class)))
      })
  @UnauthorizedApiResponse
  @ForbiddenApiResponse
  @NotFoundApiResponse
  @GetMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
  public DataApiResponse<RentalDTO> show(@PathVariable UUID id) {
    return DataApiResponse.success(
        rentalService.findById(id), "Lấy chi tiết phiếu mượn thành công");
  }

  @Operation(
      summary = "Tạo phiếu mượn mới",
      requestBody =
          @io.swagger.v3.oas.annotations.parameters.RequestBody(
              required = true,
              content =
                  @Content(
                      mediaType = MediaType.APPLICATION_JSON_VALUE,
                      schema = @Schema(implementation = RentalCreateDTO.class))),
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
      @Valid @RequestBody RentalCreateDTO dto, @AuthenticationPrincipal CustomUserDetails actor) {
    rentalService.create(dto, actor);
    return ApiResponse.created("Tạo phiếu mượn thành công");
  }

  @Operation(
      summary = "Xác nhận trả sách",
      parameters = @Parameter(name = "id", required = true),
      responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = DataApiResponseRentalDTO.class)))
      })
  @UnauthorizedApiResponse
  @ForbiddenApiResponse
  @NotFoundApiResponse
  @PutMapping("/{id}/return")
  @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
  public DataApiResponse<RentalDTO> returnBook(
      @PathVariable UUID id, @AuthenticationPrincipal CustomUserDetails actor) {
    return DataApiResponse.success(
        rentalService.returnBook(id, actor), "Xác nhận trả sách thành công");
  }

  @Operation(
      summary = "Gia hạn sách",
      parameters = @Parameter(name = "id", required = true),
      responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = DataApiResponseRentalDTO.class)))
      })
  @UnauthorizedApiResponse
  @ForbiddenApiResponse
  @NotFoundApiResponse
  @PostMapping("/{id}/renew-book")
  @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
  public DataApiResponse<RentalDTO> renewBook(
      @PathVariable UUID id, @AuthenticationPrincipal CustomUserDetails actor) {
    return DataApiResponse.success(rentalService.renewBook(id, actor), "Gia hạn sách thành công");
  }

  @Operation(
      summary = "Báo mất sách",
      parameters = @Parameter(name = "id", required = true),
      responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = DataApiResponseRentalDTO.class)))
      })
  @UnauthorizedApiResponse
  @ForbiddenApiResponse
  @NotFoundApiResponse
  @PostMapping("/{id}/report-lost")
  @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
  public DataApiResponse<RentalDTO> reportLost(
      @PathVariable UUID id, @AuthenticationPrincipal CustomUserDetails actor) {
    return DataApiResponse.success(rentalService.reportLost(id, actor), "Báo mất sách thành công");
  }

  @Operation(
      summary = "Xóa phiếu mượn",
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
    rentalService.delete(id, actor);
    return ApiResponse.success("Xóa phiếu mượn thành công");
  }

  public static class PagedApiResponseRentalDTO extends PagedApiResponse<RentalDTO> {}

  public static class DataApiResponseRentalDTO extends DataApiResponse<RentalDTO> {}
}
