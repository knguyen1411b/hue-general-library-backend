package org.app.backend.modules.rental;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.app.backend.common.dto.ApiResponse;
import org.app.backend.modules.auth.security.CustomUserDetails;
import org.app.backend.modules.rental.dto.RentalCreateDTO;
import org.app.backend.modules.rental.dto.RentalPreviewDTO;
import org.app.backend.modules.rental.dto.RentalRenewDTO;
import org.app.backend.modules.rental.dto.RentalResponseDTO;
import org.app.backend.modules.rental.dto.RentalReturnDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/rentals")
@RequiredArgsConstructor
@Tag(name = "Rental Management", description = "Quản lý luồng mượn, trả và gia hạn sách")
public class RentalV1Controller {

  private final RentalService rentalService;

  @Operation(summary = "Cho mượn sách", description = "Độc giả hoặc Thủ thư cho độc giả mượn sách")
  @PostMapping("/borrow")
  @ResponseStatus(HttpStatus.CREATED)
  @PreAuthorize("hasAnyRole('USER','LIBRARIAN')")
  public ApiResponse rentBooks(
      @Valid @RequestBody RentalCreateDTO dto, @AuthenticationPrincipal CustomUserDetails actor) {
    rentalService.rentBooks(dto, actor);
    return ApiResponse.created(RentalMessage.BORROW_SUCCESS.getMessage());
  }

  @Operation(
      summary = "Xem trước thông tin trả sách",
      description = "Quét mã vạch để xem thông tin mượn trước khi xác nhận trả sách")
  @GetMapping("/return/preview/{barcode}")
  @ResponseStatus(HttpStatus.OK)
  @PreAuthorize("hasRole('LIBRARIAN')")
  public RentalPreviewDTO getReturnPreview(@PathVariable String barcode) {
    return rentalService.getReturnPreview(barcode);
  }

  @Operation(
      summary = "Trả sách",
      description = "Thủ thư xác nhận trả sách từ độc giả tại thư viện")
  @PostMapping("/return")
  @ResponseStatus(HttpStatus.OK)
  @PreAuthorize("hasRole('LIBRARIAN')")
  public ApiResponse returnBooks(
      @Valid @RequestBody RentalReturnDTO dto, @AuthenticationPrincipal CustomUserDetails actor) {
    rentalService.returnBooks(dto, actor);
    return ApiResponse.success(RentalMessage.RETURN_SUCCESS.getMessage());
  }

  @Operation(summary = "Gia hạn sách", description = "Thủ thư gia hạn sách cho độc giả")
  @PostMapping("/renew")
  @ResponseStatus(HttpStatus.OK)
  @PreAuthorize("hasRole('LIBRARIAN')")
  public ApiResponse renewBook(
      @Valid @RequestBody RentalRenewDTO dto, @AuthenticationPrincipal CustomUserDetails actor) {
    rentalService.renewBook(dto, actor);
    return ApiResponse.success(RentalMessage.RENEW_SUCCESS.getMessage());
  }

  @Operation(summary = "Xem lịch sử mượn sách của bản thân (Độc giả)")
  @GetMapping("/my-history")
  public Page<RentalResponseDTO> getMyHistory(
      @AuthenticationPrincipal CustomUserDetails actor, Pageable pageable) {
    return rentalService.getMyRentals(actor.getId(), pageable);
  }

  @Operation(summary = "Xem toàn bộ lịch sử mượn trả hệ thống (Thủ thư)")
  @GetMapping("/all")
  @PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
  public Page<RentalResponseDTO> getAllRentals(Pageable pageable) {
    return rentalService.getAllRentals(pageable);
  }
}
