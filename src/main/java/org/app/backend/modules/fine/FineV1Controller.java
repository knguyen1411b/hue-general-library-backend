package org.app.backend.modules.fine;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.app.backend.modules.auth.security.CustomUserDetails;
import org.app.backend.modules.fine.dto.FineResponseDTO;
import org.app.backend.modules.fine.enums.FineStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/fines")
@RequiredArgsConstructor
@Tag(name = "Fine Management", description = "Quản lý đóng phạt và xem danh sách nợ")
public class FineV1Controller {

  private final FineService fineService;

  @Operation(summary = "Xem danh sách nợ của bản thân (Dành cho độc giả)")
  @GetMapping("/my-fines")
  public List<FineResponseDTO> getMyFines(
      @AuthenticationPrincipal CustomUserDetails actor,
      @RequestParam(defaultValue = "UNPAID") FineStatus status) {
    return fineService.getFinesByUserId(actor.getId(), status);
  }

  @Operation(summary = "Xem danh sách nợ toàn hệ thống (Dành cho thủ thư)")
  @GetMapping("/all")
  @PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
  public Page<FineResponseDTO> getAllFines(
      @RequestParam(defaultValue = "UNPAID") FineStatus status, Pageable pageable) {
    return fineService.getAllFines(status, pageable);
  }

  @Operation(summary = "Thanh toán/Nộp phạt", description = "Thủ thư xác nhận đã thu tiền phạt")
  @PatchMapping("/{fineId}/pay")
  @ResponseStatus(HttpStatus.OK)
  @PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
  public void processPayment(
      @PathVariable UUID fineId, @AuthenticationPrincipal CustomUserDetails actor) {
    fineService.processPayment(fineId, actor);
  }

  @Operation(
      summary = "Hủy phiếu phạt",
      description = "Dành cho trường hợp hệ thống/thủ thư ghi nhận nhầm")
  @PatchMapping("/{fineId}/cancel")
  @ResponseStatus(HttpStatus.OK)
  @PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
  public void cancelFine(
      @PathVariable UUID fineId,
      @RequestParam String reason,
      @AuthenticationPrincipal CustomUserDetails actor) {
    fineService.cancelFine(fineId, reason, actor);
  }
}
