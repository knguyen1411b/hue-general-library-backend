package org.app.backend.modules.subscription;

import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.app.backend.common.dto.ApiResponse;
import org.app.backend.common.dto.DataApiResponse;
import org.app.backend.common.dto.PagedApiResponse;
import org.app.backend.modules.auth.security.CustomUserDetails;
import org.app.backend.modules.subscription.dto.SubscriptionCreateDTO;
import org.app.backend.modules.subscription.dto.SubscriptionDTO;
import org.app.backend.modules.subscription.dto.SubscriptionUpdateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/subscriptions")
@RequiredArgsConstructor
public class SubscriptionV1Controller {

  private final SubscriptionService subscriptionService;

  @GetMapping
  @PreAuthorize("hasRole('ADMIN') or hasRole('LIBRARIAN')")
  public ResponseEntity<PagedApiResponse<SubscriptionDTO>> findAll(
      @PageableDefault(size = 20) Pageable pageable) {
    Page<SubscriptionDTO> subscriptions = subscriptionService.findAll(pageable);
    return ResponseEntity.ok(
        PagedApiResponse.success(subscriptions, "Lấy danh sách gói đăng ký thành công"));
  }

  @GetMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN') or hasRole('LIBRARIAN') or hasRole('USER')")
  public ResponseEntity<DataApiResponse<SubscriptionDTO>> findById(@PathVariable UUID id) {
    SubscriptionDTO subscription = subscriptionService.findById(id);
    return ResponseEntity.ok(
        DataApiResponse.success(subscription, "Lấy thông tin gói đăng ký thành công"));
  }

  @GetMapping("/key/{key}")
  @PreAuthorize("hasRole('ADMIN') or hasRole('LIBRARIAN') or hasRole('USER')")
  public ResponseEntity<DataApiResponse<SubscriptionDTO>> findByKey(@PathVariable String key) {
    SubscriptionDTO subscription = subscriptionService.findByKey(key);
    return ResponseEntity.ok(
        DataApiResponse.success(subscription, "Lấy thông tin gói đăng ký theo key thành công"));
  }

  @PostMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ApiResponse> create(
      @Valid @RequestBody SubscriptionCreateDTO dto,
      @AuthenticationPrincipal CustomUserDetails actor) {
    subscriptionService.create(dto, actor);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ApiResponse.created("Tạo gói đăng ký thành công"));
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ApiResponse> update(
      @PathVariable UUID id,
      @Valid @RequestBody SubscriptionUpdateDTO dto,
      @AuthenticationPrincipal CustomUserDetails actor) {
    subscriptionService.update(id, dto, actor);
    return ResponseEntity.ok(ApiResponse.success("Cập nhật gói đăng ký thành công"));
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ApiResponse> delete(
      @PathVariable UUID id, @AuthenticationPrincipal CustomUserDetails actor) {
    subscriptionService.delete(id, actor);
    return ResponseEntity.ok(ApiResponse.success("Xóa gói đăng ký thành công"));
  }
}
