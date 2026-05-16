package org.app.backend.modules.warehouse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.app.backend.common.dto.ApiResponse;
import org.app.backend.common.dto.DataApiResponse;
import org.app.backend.common.swagger.BadRequestApiResponse;
import org.app.backend.common.swagger.ForbiddenApiResponse;
import org.app.backend.common.swagger.NotFoundApiResponse;
import org.app.backend.common.swagger.UnauthorizedApiResponse;
import org.app.backend.modules.auth.security.CustomUserDetails;
import org.app.backend.modules.warehouse.dto.AddShelfDTO;
import org.app.backend.modules.warehouse.dto.AisleCreateDTO;
import org.app.backend.modules.warehouse.dto.AisleDTO;
import org.app.backend.modules.warehouse.dto.AisleUpdateDTO;
import org.app.backend.modules.warehouse.dto.FloorCreateDTO;
import org.app.backend.modules.warehouse.dto.FloorDTO;
import org.app.backend.modules.warehouse.dto.FloorUpdateDTO;
import org.app.backend.modules.warehouse.dto.PositionDTO;
import org.app.backend.modules.warehouse.dto.ShelfDTO;
import org.app.backend.modules.warehouse.dto.SimpleFloorDTO;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/warehouse")
@RequiredArgsConstructor
@Tag(name = "Kho (V1)")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class WarehouseV1Controller {
  WarehouseService warehouseService;

  @Operation(summary = "Lấy cấu trúc kho hàng dạng cây")
  @UnauthorizedApiResponse
  @ForbiddenApiResponse
  @GetMapping("/tree")
  public DataApiResponse<List<FloorDTO>> getTree() {
    return DataApiResponse.success(
        warehouseService.getWarehouseTree(), WarehouseMessage.INDEX_SUCCESS.getMessage());
  }

  @Operation(
      summary = "Tạo tầng kho",
      requestBody =
          @RequestBody(
              required = true,
              content =
                  @Content(
                      mediaType = "application/json",
                      schema = @Schema(implementation = FloorCreateDTO.class))))
  @UnauthorizedApiResponse
  @ForbiddenApiResponse
  @BadRequestApiResponse
  @PostMapping("/floors")
  public DataApiResponse<FloorDTO> createFloor(
      @Valid @org.springframework.web.bind.annotation.RequestBody FloorCreateDTO dto,
      @AuthenticationPrincipal CustomUserDetails actor) {
    return DataApiResponse.success(
        warehouseService.createFloor(dto, actor),
        WarehouseMessage.FLOOR_CREATE_SUCCESS.getMessage());
  }

  @Operation(summary = "Cập nhật tầng kho")
  @UnauthorizedApiResponse
  @ForbiddenApiResponse
  @BadRequestApiResponse
  @NotFoundApiResponse
  @PutMapping("/floors/{id}")
  public DataApiResponse<FloorDTO> updateFloor(
      @PathVariable UUID id,
      @Valid @org.springframework.web.bind.annotation.RequestBody FloorUpdateDTO dto,
      @AuthenticationPrincipal CustomUserDetails actor) {
    return DataApiResponse.success(
        warehouseService.updateFloor(id, dto, actor),
        WarehouseMessage.FLOOR_UPDATE_SUCCESS.getMessage());
  }

  @Operation(summary = "Xóa tầng kho")
  @UnauthorizedApiResponse
  @ForbiddenApiResponse
  @NotFoundApiResponse
  @DeleteMapping("/floors/{id}")
  public ApiResponse deleteFloor(
      @PathVariable UUID id, @AuthenticationPrincipal CustomUserDetails actor) {
    warehouseService.deleteFloor(id, actor);
    return ApiResponse.success(WarehouseMessage.FLOOR_DELETE_SUCCESS.getMessage());
  }

  @Operation(summary = "Lấy chi tiết tầng kho")
  @UnauthorizedApiResponse
  @ForbiddenApiResponse
  @NotFoundApiResponse
  @GetMapping("/floors/{id}")
  public DataApiResponse<FloorDTO> getFloorById(@PathVariable UUID id) {
    return DataApiResponse.success(
        warehouseService.getFloorById(id), WarehouseMessage.FLOOR_SHOW_SUCCESS.getMessage());
  }

  @Operation(summary = "Lấy danh sách tầng kho")
  @UnauthorizedApiResponse
  @ForbiddenApiResponse
  @GetMapping("/floors")
  public DataApiResponse<List<SimpleFloorDTO>> getAllFloors() {
    return DataApiResponse.success(
        warehouseService.getAllFloors(), WarehouseMessage.FLOOR_LIST_SUCCESS.getMessage());
  }

  @Operation(summary = "Tạo dãy kệ")
  @UnauthorizedApiResponse
  @ForbiddenApiResponse
  @BadRequestApiResponse
  @PostMapping("/aisles")
  public DataApiResponse<AisleDTO> createAisle(
      @Valid @org.springframework.web.bind.annotation.RequestBody AisleCreateDTO dto,
      @AuthenticationPrincipal CustomUserDetails actor) {
    return DataApiResponse.success(
        warehouseService.createAisle(dto, actor),
        WarehouseMessage.AISLE_CREATE_SUCCESS.getMessage());
  }

  @Operation(summary = "Cập nhật dãy kệ")
  @UnauthorizedApiResponse
  @ForbiddenApiResponse
  @BadRequestApiResponse
  @NotFoundApiResponse
  @PutMapping("/aisles/{id}")
  public DataApiResponse<AisleDTO> updateAisle(
      @PathVariable UUID id,
      @Valid @org.springframework.web.bind.annotation.RequestBody AisleUpdateDTO dto,
      @AuthenticationPrincipal CustomUserDetails actor) {
    return DataApiResponse.success(
        warehouseService.updateAisle(id, dto, actor),
        WarehouseMessage.AISLE_UPDATE_SUCCESS.getMessage());
  }

  @Operation(summary = "Xóa dãy kệ")
  @UnauthorizedApiResponse
  @ForbiddenApiResponse
  @NotFoundApiResponse
  @DeleteMapping("/aisles/{id}")
  public ApiResponse deleteAisle(
      @PathVariable UUID id, @AuthenticationPrincipal CustomUserDetails actor) {
    warehouseService.deleteAisle(id, actor);
    return ApiResponse.success(WarehouseMessage.AISLE_DELETE_SUCCESS.getMessage());
  }

  @Operation(summary = "Lấy chi tiết dãy kệ")
  @UnauthorizedApiResponse
  @ForbiddenApiResponse
  @NotFoundApiResponse
  @GetMapping("/aisles/{id}")
  public DataApiResponse<AisleDTO> getAisleById(@PathVariable UUID id) {
    return DataApiResponse.success(
        warehouseService.getAisleById(id), WarehouseMessage.AISLE_SHOW_SUCCESS.getMessage());
  }

  @Operation(summary = "Lấy danh sách dãy kệ")
  @UnauthorizedApiResponse
  @ForbiddenApiResponse
  @GetMapping("/aisles")
  public DataApiResponse<List<AisleDTO>> getAllAisles() {
    return DataApiResponse.success(
        warehouseService.getAllAisles(), WarehouseMessage.AISLE_LIST_SUCCESS.getMessage());
  }

  @Operation(
      summary = "Thêm kệ mới",
      parameters = {@Parameter(name = "dto", description = "Dữ liệu thêm kệ")})
  @UnauthorizedApiResponse
  @ForbiddenApiResponse
  @BadRequestApiResponse
  @PostMapping("/shelves")
  public DataApiResponse<ShelfDTO> createShelf(
      @Valid @org.springframework.web.bind.annotation.RequestBody AddShelfDTO dto,
      @AuthenticationPrincipal CustomUserDetails actor) {
    return DataApiResponse.success(
        warehouseService.createShelf(dto, actor),
        WarehouseMessage.SHELF_CREATE_SUCCESS.getMessage());
  }

  @Operation(summary = "Xóa kệ theo ID")
  @UnauthorizedApiResponse
  @ForbiddenApiResponse
  @NotFoundApiResponse
  @DeleteMapping("/shelves/{id}")
  public ApiResponse deleteShelf(
      @PathVariable UUID id, @AuthenticationPrincipal CustomUserDetails actor) {
    warehouseService.deleteShelf(id, actor);
    return ApiResponse.success(WarehouseMessage.SHELF_DELETE_SUCCESS.getMessage());
  }

  @Operation(summary = "Lấy danh sách kệ")
  @UnauthorizedApiResponse
  @ForbiddenApiResponse
  @GetMapping("/shelves")
  public DataApiResponse<List<ShelfDTO>> getAllShelves() {
    return DataApiResponse.success(
        warehouseService.getAllShelves(), WarehouseMessage.SHELF_LIST_SUCCESS.getMessage());
  }

  @Operation(summary = "Lay danh sach vi tri trong ke")
  @UnauthorizedApiResponse
  @ForbiddenApiResponse
  @NotFoundApiResponse
  @GetMapping("/shelves/{shelfId}/positions")
  public DataApiResponse<List<PositionDTO>> getPositionsByShelfId(@PathVariable UUID shelfId) {
    return DataApiResponse.success(
        warehouseService.getPositionsByShelfId(shelfId), "Lay danh sach vi tri thanh cong");
  }
}
