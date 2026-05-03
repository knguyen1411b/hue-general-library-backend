package org.app.backend.modules.book;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
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
import org.app.backend.common.swagger.NotFoundApiResponse;
import org.app.backend.common.swagger.UnauthorizedApiResponse;
import org.app.backend.modules.auth.security.CustomUserDetails;
import org.app.backend.modules.book.dto.*;
import org.app.backend.modules.book.dto.BookFilterDTO;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/books")
@RequiredArgsConstructor
@Tag(name = "Quản lý đầu sách (V1)")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookV1Controller {
  BookService bookService;

  @Operation(
      summary = "Lấy danh sách đầu sách có phân trang",
      responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = PagedApiResponse.class)))
      })
  @BadRequestApiResponse
  @UnauthorizedApiResponse
  @GetMapping
  public PagedApiResponse<BookDTO> findAll(
      @ParameterObject BookFilterDTO filter, @ParameterObject Pageable pageable) {
    return PagedApiResponse.success(
        bookService.findAll(filter, pageable), BookMessage.INDEX_SUCCESS.getMessage());
  }

  @Operation(
      summary = "Xem chi tiết đầu sách",
      responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = PagedApiResponse.class)))
      })
  @NotFoundApiResponse
  @UnauthorizedApiResponse
  @GetMapping("/{id}")
  public DataApiResponse<BookDTO> findById(@PathVariable UUID id) {
    return DataApiResponse.success(bookService.findById(id), BookMessage.SHOW_SUCCESS.getMessage());
  }

  @Operation(
      summary = "Tạo mới đầu sách (Gửi kèm file ảnh)",
      requestBody =
          @RequestBody(
              required = true,
              description = "Dữ liệu tạo mới đầu sách dưới dạng multipart/form-data",
              content =
                  @Content(
                      mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                      schema = @Schema(implementation = BookCreateDTO.class))),
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
  public ApiResponse create(
      @Valid @ModelAttribute BookCreateDTO dto, @AuthenticationPrincipal CustomUserDetails actor) {
    bookService.create(dto, actor);
    return ApiResponse.created(BookMessage.CREATE_SUCCESS.getMessage());
  }

  @Operation(
      summary = "Cập nhật thông tin đầu sách",
      requestBody =
          @RequestBody(
              required = true,
              description = "Dữ liệu cập nhật đầu sách dưới dạng multipart/form-data",
              content =
                  @Content(
                      mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                      schema = @Schema(implementation = BookUpdateDTO.class))),
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
  @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ApiResponse update(
      @PathVariable UUID id,
      @Valid @ModelAttribute BookUpdateDTO dto,
      @AuthenticationPrincipal CustomUserDetails actor) {
    bookService.update(id, dto, actor);
    return ApiResponse.success(BookMessage.UPDATE_SUCCESS.getMessage());
  }

  @Operation(
      summary = "Xóa đầu sách (Soft Delete)",
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
    bookService.delete(id, actor);
    return ApiResponse.success(BookMessage.DELETE_SUCCESS.getMessage());
  }
}
