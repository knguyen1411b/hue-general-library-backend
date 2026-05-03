package org.app.backend.modules.category;

import io.swagger.v3.oas.annotations.Operation;
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
import org.app.backend.modules.category.dto.*;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
@Tag(name = "Danh mục sách (V1)")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryV1Controller {

  CategoryService categoryService;

  @Operation(summary = "Lấy danh sách danh mục (có phân trang & lọc)")
  @GetMapping
  public PagedApiResponse<CategoryDTO> index(
      @ParameterObject CategoryFilterDTO filter, @ParameterObject Pageable pageable) {

    Page<CategoryDTO> data = categoryService.findAll(filter, pageable);
    return PagedApiResponse.success(data, CategoryMessage.INDEX_SUCCESS.getMessage());
  }

  @Operation(summary = "Lấy chi tiết danh mục theo ID")
  @NotFoundApiResponse
  @GetMapping("/{id}")
  public DataApiResponse<CategoryDTO> show(@PathVariable UUID id) {
    return DataApiResponse.success(
        categoryService.findById(id), CategoryMessage.SHOW_SUCCESS.getMessage());
  }

  @Operation(summary = "Tạo mới thể danh mục (Chỉ Thủ thư)")
  @UnauthorizedApiResponse
  @ForbiddenApiResponse
  @BadRequestApiResponse
  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
  public ApiResponse create(
      @Valid @RequestBody CategoryCreateDTO dto, @AuthenticationPrincipal CustomUserDetails actor) {
    categoryService.create(dto, actor);
    return ApiResponse.created(CategoryMessage.CREATE_SUCCESS.getMessage());
  }

  @Operation(summary = "Cập nhật danh mục theo ID (Chỉ Thủ thư)")
  @UnauthorizedApiResponse
  @ForbiddenApiResponse
  @BadRequestApiResponse
  @NotFoundApiResponse
  @PatchMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ApiResponse update(
      @PathVariable UUID id,
      @Valid @RequestBody CategoryUpdateDTO dto,
      @AuthenticationPrincipal CustomUserDetails actor) {
    categoryService.update(id, dto, actor);
    return ApiResponse.success(CategoryMessage.UPDATE_SUCCESS.getMessage());
  }

  @Operation(summary = "Xóa danh mục theo ID (Chỉ Thủ thư)")
  @UnauthorizedApiResponse
  @ForbiddenApiResponse
  @NotFoundApiResponse
  @DeleteMapping("/{id}")
  public ApiResponse delete(
      @PathVariable UUID id, @AuthenticationPrincipal CustomUserDetails actor) {
    categoryService.delete(id, actor);
    return ApiResponse.success(CategoryMessage.DELETE_SUCCESS.getMessage());
  }
}
