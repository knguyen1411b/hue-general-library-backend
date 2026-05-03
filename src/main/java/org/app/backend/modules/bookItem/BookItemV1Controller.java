package org.app.backend.modules.bookItem;

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
import org.app.backend.modules.bookItem.dto.*;
import org.app.backend.modules.bookItem.filter.BookItemFilterDTO;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/book-items")
@RequiredArgsConstructor
@Tag(name = "Bản sách (V1)")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookItemV1Controller {
  BookItemService bookItemService;

  @Operation(
      summary = "Lấy danh sách bản sách có phân trang",
      responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = PagedApiResponseBookItemDTO.class)))
      })
  @BadRequestApiResponse
  @UnauthorizedApiResponse
  @ForbiddenApiResponse
  @GetMapping
  public PagedApiResponse<BookItemDTO> index(
      @ParameterObject BookItemFilterDTO filter, @ParameterObject Pageable pageable) {
    return PagedApiResponse.success(
        bookItemService.findAll(filter, pageable), BookItemMessage.INDEX_SUCCESS.getMessage());
  }

  @Operation(
      summary = "Lấy chi tiết bản sách theo ID",
      parameters = {@Parameter(name = "id", description = "ID của bản sách", required = true)},
      responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = DataApiResponseBookItemDTO.class)))
      })
  @NotFoundApiResponse
  @UnauthorizedApiResponse
  @ForbiddenApiResponse
  @GetMapping("/{id}")
  public DataApiResponse<BookItemDTO> show(@PathVariable UUID id) {
    return DataApiResponse.success(
        bookItemService.findById(id), BookItemMessage.SHOW_SUCCESS.getMessage());
  }

  @Operation(
      summary = "Tạo mới bản sách",
      requestBody =
          @RequestBody(
              required = true,
              content =
                  @Content(
                      mediaType = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
                      schema = @Schema(implementation = BookItemCreateDTO.class))),
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
  @PostMapping(consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
  public ApiResponse create(
      @Valid @ModelAttribute BookItemCreateDTO dto,
      @AuthenticationPrincipal CustomUserDetails actor) {
    bookItemService.create(dto, actor);
    return ApiResponse.created(BookItemMessage.CREATE_SUCCESS.getMessage());
  }

  @Operation(
      summary = "Cập nhật bản sách theo ID",
      parameters = {
        @Parameter(name = "id", description = "ID của bản sách cần cập nhật", required = true)
      },
      requestBody =
          @RequestBody(
              required = true,
              description = "Dữ liệu cập nhật bản sách",
              content =
                  @Content(
                      mediaType = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
                      schema = @Schema(implementation = BookItemUpdateDTO.class))),
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
  @PatchMapping(value = "/{id}", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
  public ApiResponse update(
      @PathVariable UUID id,
      @Valid @ModelAttribute BookItemUpdateDTO dto,
      @AuthenticationPrincipal CustomUserDetails actor) {
    bookItemService.update(id, dto, actor);
    return ApiResponse.success(BookItemMessage.UPDATE_SUCCESS.getMessage());
  }

  @Operation(
      summary = "Xóa bản sách theo ID",
      description = "Xóa bản sách dựa trên ID.",
      parameters = {
        @Parameter(name = "id", description = "ID của bản sách cần xóa", required = true)
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
    bookItemService.delete(id, actor);
    return ApiResponse.success(BookItemMessage.DELETE_SUCCESS.getMessage());
  }

  public static class PagedApiResponseBookItemDTO extends PagedApiResponse<BookItemDTO> {}

  public static class DataApiResponseBookItemDTO extends DataApiResponse<BookItemDTO> {}
}
