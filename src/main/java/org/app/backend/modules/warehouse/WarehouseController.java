package org.app.backend.modules.warehouse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.app.backend.common.dto.ApiResponse;
import org.app.backend.common.dto.DataApiResponse;
import org.app.backend.common.dto.PagedApiResponse;
import org.app.backend.common.swagger.BadRequestApiResponse;
import org.app.backend.common.swagger.ForbiddenApiResponse;
import org.app.backend.common.swagger.NotFoundApiResponse;
import org.app.backend.common.swagger.UnauthorizedApiResponse;
import org.app.backend.modules.warehouse.dto.AddShelfDTO;
import org.app.backend.modules.warehouse.entity.Floor;
import org.app.backend.modules.warehouse.entity.Shelf;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/warehouse")
@RequiredArgsConstructor
@Tag(name = "Warehouse (V1)", description = "API quản lý kho hàng")
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class WarehouseController {
  WarehouseService warehouseService;

  @Operation(
      summary = "Lấy cấu trúc kho hàng dưới dạng cây",
      description = "Trả về cấu trúc kho hàng bao gồm các tầng và kệ dưới dạng cây.",
      parameters = {@Parameter(name = "pageable", description = "Thông tin phân trang")},
      responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = PagedApiResponseFloor.class)))
      })
  @UnauthorizedApiResponse
  @ForbiddenApiResponse
  @NotFoundApiResponse
  @GetMapping("/tree")
  public PagedApiResponse<Floor> getTree(@ParameterObject Pageable pageable) {
    return PagedApiResponse.success(
        warehouseService.getWarehouseTree(pageable), WarehouseMessage.INDEX_SUCCESS.getMessage());
  }

  @Operation(
      summary = "Thêm kệ mới",
      description = "Thêm một kệ mới vào kho hàng.",
      requestBody =
          @io.swagger.v3.oas.annotations.parameters.RequestBody(
              required = true,
              description = "Dữ liệu của kệ mới cần thêm",
              content =
                  @Content(
                      mediaType = "application/json",
                      schema = @Schema(implementation = AddShelfDTO.class))),
      responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = DataApiResponseShelf.class)))
      })
  @UnauthorizedApiResponse
  @ForbiddenApiResponse
  @NotFoundApiResponse
  @BadRequestApiResponse
  @PostMapping("/shelves")
  public DataApiResponse<Shelf> addShelf(@RequestBody AddShelfDTO dto) {
    return DataApiResponse.success(
        warehouseService.createShelf(dto), WarehouseMessage.CREATE_SUCCESS.getMessage());
  }

  @Operation(
      summary = "Xóa kệ theo ID",
      description = "Xóa kệ dựa trên ID.",
      parameters = {@Parameter(name = "id", description = "ID của kệ cần xóa", required = true)},
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
  @DeleteMapping("/shelves/{id}")
  public ApiResponse deleteShelf(@PathVariable UUID id) {
    warehouseService.deleteShelf(id);
    return ApiResponse.success(WarehouseMessage.DELETE_SUCCESS.getMessage());
  }

  public static class PagedApiResponseFloor extends PagedApiResponse<Floor> {}

  public static class DataApiResponseShelf extends DataApiResponse<Shelf> {}
}
