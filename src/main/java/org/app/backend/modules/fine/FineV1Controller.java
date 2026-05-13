package org.app.backend.modules.fine;

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
import org.app.backend.modules.fine.dto.FineCreateDTO;
import org.app.backend.modules.fine.dto.FineDTO;
import org.app.backend.modules.fine.enums.FineStatus;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/fines")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Phiếu phạt (V1)")
public class FineV1Controller {
  FineService fineService;

  @Operation(
      summary = "Lấy danh sách phiếu phạt có phân trang",
      responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = PagedApiResponseFineDTO.class)))
      })
  @UnauthorizedApiResponse
  @ForbiddenApiResponse
  @GetMapping
  @PreAuthorize("hasAnyRole('ADMIN','MANAGER','USER')")
  public PagedApiResponse<FineDTO> index(
      @RequestParam(required = false) UUID rentalId,
      @RequestParam(required = false) FineStatus status,
      @ParameterObject Pageable pageable) {
    return PagedApiResponse.success(
        fineService.findAll(pageable, rentalId, status), "Lấy danh sách phiếu phạt thành công");
  }

  @Operation(
      summary = "Xem chi tiết phiếu phạt",
      parameters = @Parameter(name = "id", required = true),
      responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = DataApiResponseFineDTO.class)))
      })
  @UnauthorizedApiResponse
  @ForbiddenApiResponse
  @NotFoundApiResponse
  @GetMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
  public DataApiResponse<FineDTO> show(@PathVariable UUID id) {
    return DataApiResponse.success(fineService.findById(id), "Lấy chi tiết phiếu phạt thành công");
  }

  @Operation(
      summary = "Tạo phiếu phạt mới",
      requestBody =
          @io.swagger.v3.oas.annotations.parameters.RequestBody(
              required = true,
              content =
                  @Content(
                      mediaType = MediaType.APPLICATION_JSON_VALUE,
                      schema = @Schema(implementation = FineCreateDTO.class))),
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
      @Valid @RequestBody FineCreateDTO dto, @AuthenticationPrincipal CustomUserDetails actor) {
    fineService.create(dto, actor);
    return ApiResponse.created("Tạo phiếu phạt thành công");
  }

  @Operation(
      summary = "Xác nhận thanh toán phiếu phạt",
      parameters = @Parameter(name = "id", required = true),
      responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = DataApiResponseFineDTO.class)))
      })
  @UnauthorizedApiResponse
  @ForbiddenApiResponse
  @NotFoundApiResponse
  @PutMapping("/{id}/pay")
  @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
  public DataApiResponse<FineDTO> pay(
      @PathVariable UUID id, @AuthenticationPrincipal CustomUserDetails actor) {
    return DataApiResponse.success(fineService.pay(id, actor), "Xác nhận thanh toán thành công");
  }

  @Operation(
      summary = "Xóa phiếu phạt",
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
    fineService.delete(id, actor);
    return ApiResponse.success("Xóa phiếu phạt thành công");
  }

  public static class PagedApiResponseFineDTO extends PagedApiResponse<FineDTO> {}

  public static class DataApiResponseFineDTO extends DataApiResponse<FineDTO> {}
}
