package org.app.backend.modules.usersubscription;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.app.backend.modules.subscription.Subscription;
import org.app.backend.modules.subscription.SubscriptionRepository;
import org.app.backend.modules.user.User;
import org.app.backend.modules.user.UserRepository;
import org.app.backend.modules.usersubscription.dto.UserSubscriptionCreateDTO;
import org.app.backend.modules.usersubscription.dto.UserSubscriptionResponseDTO;
import org.app.backend.modules.usersubscription.dto.UserSubscriptionUpdateDTO;
import org.app.backend.modules.usersubscription.exception.SubscriptionNotFoundException;
import org.app.backend.modules.usersubscription.exception.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user-subscriptions")
@Validated
public class UserSubscriptionV1Controller {

  private static final Logger logger = LoggerFactory.getLogger(UserSubscriptionV1Controller.class);

  private final UserSubscriptionService userSubscriptionService;
  private final UserRepository userRepository;
  private final SubscriptionRepository subscriptionRepository;

  UserSubscriptionV1Controller(
      UserSubscriptionService userSubscriptionService,
      UserRepository userRepository,
      SubscriptionRepository subscriptionRepository) {
    this.userSubscriptionService = userSubscriptionService;
    this.userRepository = userRepository;
    this.subscriptionRepository = subscriptionRepository;
  }

  @PostMapping
  public ResponseEntity<UserSubscriptionResponseDTO> createUserSubscription(
      @Valid @RequestBody UserSubscriptionCreateDTO requestDto) {
    logger.info(
        "Creating user subscription for user: {}, subscription: {}",
        requestDto.getUserId(),
        requestDto.getSubscriptionId());

    // Fetch User and Subscription from database
    User user =
        userRepository
            .findById(requestDto.getUserId())
            .orElseThrow(() -> new UserNotFoundException(UserSubscriptionMessage.USER_NOT_FOUND));

    Subscription subscription =
        subscriptionRepository
            .findById(requestDto.getSubscriptionId())
            .orElseThrow(
                () ->
                    new SubscriptionNotFoundException(
                        UserSubscriptionMessage.SUBSCRIPTION_NOT_FOUND));

    UserSubscription userSubscription = new UserSubscription();
    userSubscription.setUser(user);
    userSubscription.setSubscription(subscription);
    userSubscription.setStartDate(requestDto.getStartDate());
    userSubscription.setEndDate(requestDto.getEndDate());
    userSubscription.setStatus(requestDto.getStatus());
    userSubscription.setMaxBooks(requestDto.getMaxBooks());
    userSubscription.setPrice(requestDto.getPrice());

    UserSubscription created = userSubscriptionService.create(userSubscription);
    return ResponseEntity.status(HttpStatus.CREATED).body(mapToResponseDto(created));
  }

  @GetMapping("/{id}")
  public ResponseEntity<UserSubscriptionResponseDTO> getUserSubscriptionById(
      @PathVariable UUID id) {
    logger.debug("Getting user subscription with id: {}", id);
    UserSubscription userSubscription = userSubscriptionService.getById(id);
    return ResponseEntity.ok(mapToResponseDto(userSubscription));
  }

  @PutMapping("/{id}")
  public ResponseEntity<UserSubscriptionResponseDTO> updateUserSubscription(
      @PathVariable UUID id, @Valid @RequestBody UserSubscriptionUpdateDTO requestDto) {
    logger.info("Updating user subscription with id: {}", id);

    UserSubscription userSubscription = new UserSubscription();
    if (requestDto.getStartDate() != null) {
      userSubscription.setStartDate(requestDto.getStartDate());
    }
    if (requestDto.getEndDate() != null) {
      userSubscription.setEndDate(requestDto.getEndDate());
    }
    if (requestDto.getStatus() != null) {
      userSubscription.setStatus(requestDto.getStatus());
    }
    if (requestDto.getMaxBooks() != null) {
      userSubscription.setMaxBooks(requestDto.getMaxBooks());
    }
    if (requestDto.getPrice() != null) {
      userSubscription.setPrice(requestDto.getPrice());
    }

    UserSubscription updated = userSubscriptionService.update(id, userSubscription);
    return ResponseEntity.ok(mapToResponseDto(updated));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteUserSubscription(@PathVariable UUID id) {
    logger.info("Deleting user subscription with id: {}", id);
    userSubscriptionService.delete(id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping
  public ResponseEntity<Page<UserSubscriptionResponseDTO>> getAllUserSubscriptions(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size,
      @RequestParam(defaultValue = "createdAt") String sortBy,
      @RequestParam(defaultValue = "desc") String direction) {
    logger.debug("Getting all user subscriptions with pagination");
    Sort.Direction sortDirection = Sort.Direction.fromString(direction);
    Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
    Page<UserSubscription> userSubscriptions = userSubscriptionService.getAll(pageable);
    Page<UserSubscriptionResponseDTO> response = userSubscriptions.map(this::mapToResponseDto);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/all")
  public ResponseEntity<List<UserSubscriptionResponseDTO>> getAllUserSubscriptions() {
    logger.debug("Getting all user subscriptions without pagination");
    List<UserSubscription> userSubscriptions = userSubscriptionService.getAll();
    List<UserSubscriptionResponseDTO> response =
        userSubscriptions.stream().map(this::mapToResponseDto).collect(Collectors.toList());
    return ResponseEntity.ok(response);
  }

  @GetMapping("/user/{userId}")
  public ResponseEntity<List<UserSubscriptionResponseDTO>> getUserSubscriptionsByUserId(
      @PathVariable UUID userId) {
    logger.debug("Getting user subscriptions for user: {}", userId);
    List<UserSubscription> userSubscriptions = userSubscriptionService.getByUserId(userId);
    List<UserSubscriptionResponseDTO> response =
        userSubscriptions.stream().map(this::mapToResponseDto).collect(Collectors.toList());
    return ResponseEntity.ok(response);
  }

  @GetMapping("/subscription/{subscriptionId}")
  public ResponseEntity<List<UserSubscriptionResponseDTO>> getUserSubscriptionsBySubscriptionId(
      @PathVariable UUID subscriptionId) {
    logger.debug("Getting user subscriptions for subscription: {}", subscriptionId);
    List<UserSubscription> userSubscriptions =
        userSubscriptionService.getBySubscriptionId(subscriptionId);
    List<UserSubscriptionResponseDTO> response =
        userSubscriptions.stream().map(this::mapToResponseDto).collect(Collectors.toList());
    return ResponseEntity.ok(response);
  }

  @GetMapping("/status/{status}")
  public ResponseEntity<List<UserSubscriptionResponseDTO>> getUserSubscriptionsByStatus(
      @PathVariable UserSubscriptionStatus status) {
    logger.debug("Getting user subscriptions with status: {}", status);
    List<UserSubscription> userSubscriptions = userSubscriptionService.getByStatus(status);
    List<UserSubscriptionResponseDTO> response =
        userSubscriptions.stream().map(this::mapToResponseDto).collect(Collectors.toList());
    return ResponseEntity.ok(response);
  }

  @GetMapping("/user/{userId}/active")
  public ResponseEntity<List<UserSubscriptionResponseDTO>> getActiveSubscriptionsByUser(
      @PathVariable UUID userId) {
    logger.debug("Getting active subscriptions for user: {}", userId);
    List<UserSubscription> userSubscriptions =
        userSubscriptionService.getActiveSubscriptionsByUser(userId);
    List<UserSubscriptionResponseDTO> response =
        userSubscriptions.stream().map(this::mapToResponseDto).collect(Collectors.toList());
    return ResponseEntity.ok(response);
  }

  @GetMapping("/expired")
  public ResponseEntity<List<UserSubscriptionResponseDTO>> getExpiredSubscriptions() {
    logger.debug("Getting all expired subscriptions");
    List<UserSubscription> userSubscriptions = userSubscriptionService.getExpiredSubscriptions();
    List<UserSubscriptionResponseDTO> response =
        userSubscriptions.stream().map(this::mapToResponseDto).collect(Collectors.toList());
    return ResponseEntity.ok(response);
  }

  @GetMapping("/canceled")
  public ResponseEntity<List<UserSubscriptionResponseDTO>> getCanceledSubscriptions() {
    logger.debug("Getting all canceled subscriptions");
    List<UserSubscription> userSubscriptions = userSubscriptionService.getCanceledSubscriptions();
    List<UserSubscriptionResponseDTO> response =
        userSubscriptions.stream().map(this::mapToResponseDto).collect(Collectors.toList());
    return ResponseEntity.ok(response);
  }

  @PostMapping("/{id}/activate")
  public ResponseEntity<UserSubscriptionResponseDTO> activateSubscription(@PathVariable UUID id) {
    logger.info("Activating user subscription with id: {}", id);
    UserSubscription userSubscription = userSubscriptionService.activateSubscription(id);
    return ResponseEntity.ok(mapToResponseDto(userSubscription));
  }

  @PostMapping("/{id}/expire")
  public ResponseEntity<UserSubscriptionResponseDTO> expireSubscription(@PathVariable UUID id) {
    logger.info("Expiring user subscription with id: {}", id);
    UserSubscription userSubscription = userSubscriptionService.expireSubscription(id);
    return ResponseEntity.ok(mapToResponseDto(userSubscription));
  }

  @PostMapping("/{id}/cancel")
  public ResponseEntity<UserSubscriptionResponseDTO> cancelSubscription(@PathVariable UUID id) {
    logger.info("Canceling user subscription with id: {}", id);
    UserSubscription userSubscription = userSubscriptionService.cancelSubscription(id);
    return ResponseEntity.ok(mapToResponseDto(userSubscription));
  }

  @PostMapping("/{id}/renew")
  public ResponseEntity<UserSubscriptionResponseDTO> renewSubscription(
      @PathVariable UUID id,
      @RequestParam @Valid LocalDate startDate,
      @RequestParam @Valid LocalDate endDate) {
    logger.info("Renewing user subscription with id: {}", id);
    UserSubscription userSubscription =
        userSubscriptionService.renewSubscription(id, startDate, endDate);
    return ResponseEntity.ok(mapToResponseDto(userSubscription));
  }

  @GetMapping("/user/{userId}/can-subscribe/{subscriptionId}")
  public ResponseEntity<Boolean> canUserSubscribe(
      @PathVariable UUID userId, @PathVariable UUID subscriptionId) {
    logger.debug("Checking if user {} can subscribe to subscription {}", userId, subscriptionId);
    boolean canSubscribe = userSubscriptionService.canUserSubscribe(userId, subscriptionId);
    return ResponseEntity.ok(canSubscribe);
  }

  @GetMapping("/{id}/is-active")
  public ResponseEntity<Boolean> isSubscriptionActive(@PathVariable UUID id) {
    logger.debug("Checking if subscription {} is active", id);
    boolean isActive = userSubscriptionService.isSubscriptionActive(id);
    return ResponseEntity.ok(isActive);
  }

  @GetMapping("/{id}/is-expired")
  public ResponseEntity<Boolean> isSubscriptionExpired(@PathVariable UUID id) {
    logger.debug("Checking if subscription {} is expired", id);
    boolean isExpired = userSubscriptionService.isSubscriptionExpired(id);
    return ResponseEntity.ok(isExpired);
  }

  @GetMapping("/{id}/is-canceled")
  public ResponseEntity<Boolean> isSubscriptionCanceled(@PathVariable UUID id) {
    logger.debug("Checking if subscription {} is canceled", id);
    boolean isCanceled = userSubscriptionService.isSubscriptionCanceled(id);
    return ResponseEntity.ok(isCanceled);
  }

  @GetMapping("/statistics/active")
  public ResponseEntity<Long> countActiveSubscriptions() {
    logger.debug("Counting active subscriptions");
    long count = userSubscriptionService.countActiveSubscriptions();
    return ResponseEntity.ok(count);
  }

  @GetMapping("/statistics/expired")
  public ResponseEntity<Long> countExpiredSubscriptions() {
    logger.debug("Counting expired subscriptions");
    long count = userSubscriptionService.countExpiredSubscriptions();
    return ResponseEntity.ok(count);
  }

  @GetMapping("/statistics/canceled")
  public ResponseEntity<Long> countCanceledSubscriptions() {
    logger.debug("Counting canceled subscriptions");
    long count = userSubscriptionService.countCanceledSubscriptions();
    return ResponseEntity.ok(count);
  }

  @GetMapping("/statistics/user/{userId}")
  public ResponseEntity<Long> countByUser(@PathVariable UUID userId) {
    logger.debug("Counting subscriptions for user: {}", userId);
    long count = userSubscriptionService.countByUser(userId);
    return ResponseEntity.ok(count);
  }

  private UserSubscriptionResponseDTO mapToResponseDto(UserSubscription userSubscription) {
    UserSubscriptionResponseDTO dto = new UserSubscriptionResponseDTO();
    dto.setId(userSubscription.getId());
    dto.setUserId(userSubscription.getUser().getId());
    dto.setUsername(userSubscription.getUser().getUsername());
    dto.setFullName(userSubscription.getUser().getFullName());
    dto.setEmail(userSubscription.getUser().getEmail());
    dto.setSubscriptionId(userSubscription.getSubscription().getId());
    dto.setSubscriptionKey(userSubscription.getSubscription().getKey());
    dto.setSubscriptionName(userSubscription.getSubscription().getName());
    dto.setSubscriptionDurationDays(userSubscription.getSubscription().getDurationDays());
    dto.setSubscriptionOverdueFeePerDay(userSubscription.getSubscription().getOverdueFeePerDay());
    dto.setSubscriptionMaxRenewals(userSubscription.getSubscription().getMaxRenewals());
    dto.setSubscriptionCompensationRate(userSubscription.getSubscription().getCompensationRate());
    dto.setStartDate(userSubscription.getStartDate());
    dto.setEndDate(userSubscription.getEndDate());
    dto.setStatus(userSubscription.getStatus());
    dto.setMaxBooks(userSubscription.getMaxBooks());
    dto.setPrice(userSubscription.getPrice());
    return dto;
  }
}
