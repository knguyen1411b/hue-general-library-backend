package org.app.backend.modules.librarycard;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.app.backend.common.dto.ApiResponse;
import org.app.backend.common.dto.DataApiResponse;
import org.app.backend.common.dto.PagedApiResponse;
import org.app.backend.common.swagger.*;
import org.app.backend.modules.auth.security.CustomUserDetails;
import org.app.backend.modules.librarycard.dto.LibraryCardCreateDTO;
import org.app.backend.modules.librarycard.dto.LibraryCardDTO;
import org.app.backend.modules.librarycard.dto.LibraryCardUpdateDTO;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/library-cards")
@RequiredArgsConstructor
@Tag(name = "Thẻ thư viện (V1)", description = "Quản lý thẻ thư viện")
public class LibraryCardV1Controller {
  private final LibraryCardService libraryCardService;

  @Operation(
      summary = "Lấy danh sách thẻ thư viện có phân trang",
      responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = PagedApiResponseLibraryCardDTO.class)))
      })
  @UnauthorizedApiResponse
  @ForbiddenApiResponse
  @GetMapping
  @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
  public PagedApiResponse<LibraryCardDTO> index(
      @RequestParam(required = false) UUID userId,
      @RequestParam(required = false) CardStatus status,
      @ParameterObject Pageable pageable) {
    return PagedApiResponse.success(
        libraryCardService.findAll(pageable, userId, status), "Lấy danh sách thẻ thành công");
  }

  @Operation(
      summary = "Xem chi tiết thẻ thư viện",
      parameters = @Parameter(name = "id", required = true),
      responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = DataApiResponseLibraryCardDTO.class)))
      })
  @UnauthorizedApiResponse
  @ForbiddenApiResponse
  @NotFoundApiResponse
  @GetMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
  public DataApiResponse<LibraryCardDTO> show(@PathVariable UUID id) {
    return DataApiResponse.success(libraryCardService.findById(id), "Lấy chi tiết thẻ thành công");
  }

  @Operation(
      summary = "Tạo thẻ thư viện mới",
      requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
          required = true,
          content = @Content(
              mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = LibraryCardCreateDTO.class))),
      responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class)))
      })
  @UnauthorizedApiResponse
  @ForbiddenApiResponse
  @BadRequestApiResponse
  @PostMapping
  @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
  public ApiResponse create(
      @Valid @RequestBody LibraryCardCreateDTO dto, @AuthenticationPrincipal CustomUserDetails actor) {
    libraryCardService.create(dto, actor);
    return ApiResponse.created("Tạo thẻ thư viện thành công");
  }

  @Operation(
      summary = "Cập nhật thông tin thẻ thư viện",
      parameters = @Parameter(name = "id", required = true),
      requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
          required = true,
          content = @Content(
              mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = LibraryCardUpdateDTO.class))),
      responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class)))
      })
  @UnauthorizedApiResponse
  @ForbiddenApiResponse
  @BadRequestApiResponse
  @NotFoundApiResponse
  @PatchMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ApiResponse update(
      @PathVariable UUID id,
      @Valid @RequestBody LibraryCardUpdateDTO dto,
      @AuthenticationPrincipal CustomUserDetails actor) {
    libraryCardService.update(id, dto, actor);
    return ApiResponse.success("Cập nhật thẻ thành công");
  }

  @Operation(
      summary = "Khóa thẻ thư viện",
      parameters = @Parameter(name = "id", required = true),
      responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = DataApiResponseLibraryCardDTO.class)))
      })
  @UnauthorizedApiResponse
  @ForbiddenApiResponse
  @NotFoundApiResponse
  @PostMapping("/{id}/lock")
  @PreAuthorize("hasRole('ADMIN')")
  public DataApiResponse<LibraryCardDTO> lock(
      @PathVariable UUID id, @AuthenticationPrincipal CustomUserDetails actor) {
    return DataApiResponse.success(libraryCardService.lock(id, actor), "Khóa thẻ thành công");
  }

  @Operation(
      summary = "Làm lại thẻ thư viện mới",
      parameters = @Parameter(name = "id", required = true),
      responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = DataApiResponseLibraryCardDTO.class)))
      })
  @UnauthorizedApiResponse
  @ForbiddenApiResponse
  @NotFoundApiResponse
  @PostMapping("/{id}/replace")
  @PreAuthorize("hasRole('ADMIN')")
  public DataApiResponse<LibraryCardDTO> replace(
      @PathVariable UUID id, @AuthenticationPrincipal CustomUserDetails actor) {
    return DataApiResponse.success(libraryCardService.replace(id, actor), "Làm lại thẻ thành công");
  }

  @Operation(
      summary = "Xóa thẻ thư viện",
      parameters = @Parameter(name = "id", required = true),
      responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            content = @Content(
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
    libraryCardService.delete(id, actor);
    return ApiResponse.success("Xóa thẻ thành công");
  }

  public static class PagedApiResponseLibraryCardDTO extends PagedApiResponse<LibraryCardDTO> {}
  public static class DataApiResponseLibraryCardDTO extends DataApiResponse<LibraryCardDTO> {}
}
