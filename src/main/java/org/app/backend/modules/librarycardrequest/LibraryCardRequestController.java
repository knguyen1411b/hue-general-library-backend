package org.app.backend.modules.librarycardrequest;

import io.swagger.v3.oas.annotations.Operation;
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
import org.app.backend.common.swagger.BadRequestApiResponse;
import org.app.backend.common.swagger.ForbiddenApiResponse;
import org.app.backend.common.swagger.UnauthorizedApiResponse;
import org.app.backend.modules.auth.security.CustomUserDetails;
import org.app.backend.modules.librarycardrequest.dto.LibraryCardRequestDTO;
import org.app.backend.modules.librarycardrequest.dto.LibraryCardRequestUpdateDTO;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/library-cards/requests")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Yêu cầu cấp thẻ thư viện")
public class LibraryCardRequestController {

  LibraryCardRequestService requestService;

  @Operation(
      summary = "Lấy danh sách yêu cầu cấp thẻ của user hiện tại",
      responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = PagedApiResponse.class)))
      })
  @UnauthorizedApiResponse
  @GetMapping("/me")
  @PreAuthorize("hasRole('USER')")
  public PagedApiResponse<LibraryCardRequestDTO> getMyRequests(
      @AuthenticationPrincipal CustomUserDetails user, @ParameterObject Pageable pageable) {
    Page<LibraryCardRequestDTO> page = requestService.getRequestsByUserId(user.getId(), pageable);
    return PagedApiResponse.success(page, "Lấy danh sách yêu cầu cấp thẻ thành công");
  }

  @Operation(
      summary = "Lấy danh sách tất cả yêu cầu cấp thẻ (Admin/Manager)",
      responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = PagedApiResponse.class)))
      })
  @UnauthorizedApiResponse
  @ForbiddenApiResponse
  @GetMapping
  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
  public PagedApiResponse<LibraryCardRequestDTO> getAllRequests(
      @RequestParam(required = false) UUID userId,
      @RequestParam(required = false) LibraryCardRequestStatus status,
      @ParameterObject Pageable pageable) {
    Page<LibraryCardRequestDTO> page = requestService.getAllRequests(pageable, userId, status);
    return PagedApiResponse.success(page, "Lấy danh sách yêu cầu cấp thẻ thành công");
  }

  @Operation(
      summary = "Xem chi tiết yêu cầu cấp thẻ",
      responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = DataApiResponse.class)))
      })
  @UnauthorizedApiResponse
  @ForbiddenApiResponse
  @GetMapping("/{id}")
  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'USER')")
  public DataApiResponse<LibraryCardRequestDTO> getRequestById(@PathVariable UUID id) {
    LibraryCardRequestDTO dto = requestService.getRequestById(id);
    return DataApiResponse.success(dto, "Lấy thông tin yêu cầu cấp thẻ thành công");
  }

  @Operation(
      summary = "Cập nhật trạng thái yêu cầu cấp thẻ (Admin/Manager)",
      responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = DataApiResponse.class)))
      })
  @UnauthorizedApiResponse
  @ForbiddenApiResponse
  @BadRequestApiResponse
  @PatchMapping("/{id}")
  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
  public DataApiResponse<LibraryCardRequestDTO> updateRequestStatus(
      @PathVariable UUID id, @Valid @RequestBody LibraryCardRequestUpdateDTO dto) {
    LibraryCardRequestDTO updated =
        requestService.updateRequestStatus(id, dto.getStatus(), dto.getNote());
    return DataApiResponse.success(updated, "Cập nhật trạng thái yêu cầu thành công");
  }

  @Operation(
      summary = "User hủy yêu cầu cấp thẻ của chính mình",
      responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponse.class)))
      })
  @UnauthorizedApiResponse
  @BadRequestApiResponse
  @PostMapping("/{id}/cancel")
  @PreAuthorize("hasRole('USER')")
  public ApiResponse cancelRequest(
      @PathVariable UUID id, @AuthenticationPrincipal CustomUserDetails user) {
    requestService.cancelRequest(id, user.getId());
    return ApiResponse.success("Hủy yêu cầu cấp thẻ thành công");
  }
}
