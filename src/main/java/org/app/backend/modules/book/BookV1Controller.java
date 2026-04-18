package org.app.backend.modules.book;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.app.backend.common.dto.PagedApiResponse;
import org.app.backend.modules.auth.security.CustomUserDetails;
import org.app.backend.modules.book.dto.*;
import org.app.backend.modules.book.filter.BookFilterDTO;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/books")
@RequiredArgsConstructor
@Tag(name = "Book Management", description = "Quản lý đầu sách")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookV1Controller {
  BookService bookService;

  @Operation(summary = "Lấy danh sách đầu sách có phân trang")
  @GetMapping
  public PagedApiResponse<BookDTO> findAll(
      @ParameterObject BookFilterDTO filter, @ParameterObject Pageable pageable) {
    Page<BookDTO> data = bookService.findAll(filter, pageable);
    // SỬA TẠI ĐÂY: Thêm tham số message vào hàm success
    return PagedApiResponse.success(data, "Lấy danh sách đầu sách thành công");
  }

  @Operation(summary = "Xem chi tiết đầu sách")
  @GetMapping("/{id}")
  public BookDTO findById(@PathVariable UUID id) {
    return bookService.findById(id);
  }

  @Operation(summary = "Tạo mới đầu sách (Gửi kèm file ảnh)")
  @PostMapping(consumes = {"multipart/form-data"})
  @ResponseStatus(HttpStatus.CREATED)
  public void create(
      @Valid @ModelAttribute BookCreateDTO dto, @AuthenticationPrincipal CustomUserDetails actor) {
    bookService.create(dto, actor);
  }

  @Operation(summary = "Cập nhật thông tin đầu sách")
  @PutMapping(
      value = "/{id}",
      consumes = {"multipart/form-data"})
  public void update(
      @PathVariable UUID id,
      @Valid @ModelAttribute BookUpdateDTO dto,
      @AuthenticationPrincipal CustomUserDetails actor) {
    bookService.update(id, dto, actor);
  }

  @Operation(summary = "Xóa đầu sách (Soft Delete)")
  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable UUID id, @AuthenticationPrincipal CustomUserDetails actor) {
    bookService.delete(id, actor);
  }
}
