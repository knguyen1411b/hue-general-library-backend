package org.app.backend.modules.usersubscription;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.app.backend.common.exception.AppException;
import org.app.backend.modules.auth.security.CustomUserDetails;
import org.app.backend.modules.subscription.Subscription;
import org.app.backend.modules.subscription.SubscriptionRepository;
import org.app.backend.modules.user.User;
import org.app.backend.modules.user.UserRepository;
import org.app.backend.modules.user.UserRole;
import org.app.backend.modules.usersubscription.dto.UserSubscriptionCreateDTO;
import org.app.backend.modules.usersubscription.dto.UserSubscriptionResponseDTO;
import org.app.backend.modules.usersubscription.dto.UserSubscriptionUpdateDTO;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserSubscriptionServiceImpl implements UserSubscriptionService {

  UserSubscriptionRepository userSubscriptionRepository;
  SubscriptionRepository subscriptionRepository;
  UserRepository userRepository;
  ModelMapper modelMapper;

  @Override
  @Transactional
  public UserSubscriptionResponseDTO create(UserSubscriptionCreateDTO dto) {
    User managedUser =
        userRepository
            .findById(dto.getUserId())
            .orElseThrow(
                () ->
                    new AppException(
                        HttpStatus.BAD_REQUEST,
                        UserSubscriptionMessage.USER_NOT_FOUND.getMessage()));

    Subscription managedSubscription =
        subscriptionRepository
            .findById(dto.getSubscriptionId())
            .orElseThrow(
                () ->
                    new AppException(
                        HttpStatus.BAD_REQUEST,
                        UserSubscriptionMessage.SUBSCRIPTION_NOT_FOUND.getMessage()));

    LocalDate today = LocalDate.now();
    markExpiredSubscriptions(managedUser.getId(), today);

    var maybeActiveSubscription = userSubscriptionRepository.findActiveSubscriptionByUserId(managedUser.getId());
    UserSubscription activeSubscription =
        maybeActiveSubscription != null ? maybeActiveSubscription.orElse(null) : null;

    if (activeSubscription != null) {
      LocalDate extendedEndDate =
          (activeSubscription.getEndDate().isAfter(today) ? activeSubscription.getEndDate() : today)
              .plusDays(managedSubscription.getDurationDays());

      activeSubscription.setSubscription(managedSubscription);
      activeSubscription.setEndDate(extendedEndDate);
      activeSubscription.setStatus(UserSubscriptionStatus.ACTIVE);
      activeSubscription.setMaxBooks(managedSubscription.getMaxBooks());
      activeSubscription.setPrice(managedSubscription.getPrice());

      UserSubscription saved = userSubscriptionRepository.save(activeSubscription);
      return modelMapper.map(saved, UserSubscriptionResponseDTO.class);
    }

    LocalDate startDate = today;
    LocalDate endDate = startDate.plusDays(managedSubscription.getDurationDays());

    UserSubscription entity =
        UserSubscription.builder()
            .user(managedUser)
            .subscription(managedSubscription)
            .startDate(startDate)
            .endDate(endDate)
            .status(UserSubscriptionStatus.ACTIVE)
            .maxBooks(managedSubscription.getMaxBooks())
            .price(managedSubscription.getPrice())
            .build();

    UserSubscription saved = userSubscriptionRepository.save(entity);
    return modelMapper.map(saved, UserSubscriptionResponseDTO.class);
  }

  @Override
  @Transactional(readOnly = true)
  public UserSubscriptionResponseDTO getById(UUID id) {
    UserSubscription entity = getEntity(id);
    return modelMapper.map(entity, UserSubscriptionResponseDTO.class);
  }

  @Override
  @Transactional(readOnly = true)
  public List<UserSubscriptionResponseDTO> getAll() {
    return userSubscriptionRepository.findByStatusNot(UserSubscriptionStatus.DELETED).stream()
        .map(e -> modelMapper.map(e, UserSubscriptionResponseDTO.class))
        .toList();
  }

  @Override
  @Transactional(readOnly = true)
  public List<UserSubscriptionResponseDTO> getByUserId(UUID userId) {
    return userSubscriptionRepository
        .findByUser_IdAndStatusNot(userId, UserSubscriptionStatus.DELETED)
        .stream()
        .map(e -> modelMapper.map(e, UserSubscriptionResponseDTO.class))
        .toList();
  }

  @Override
  @Transactional
  public UserSubscriptionResponseDTO update(
      UUID id, UserSubscriptionUpdateDTO dto, CustomUserDetails actor) {
    UserSubscription existing = getEntity(id);
    LocalDate today = LocalDate.now();

    validateUserUpdatePermission(existing, dto, actor);
    markExpiredSubscription(existing, today);

    boolean userRenewRequest =
        actor != null
            && actor.getRole() == UserRole.USER
            && dto.getEndDate() != null
            && dto.getStatus() == null
            && dto.getStartDate() == null;

    if (dto.getStartDate() != null) {
      existing.setStartDate(dto.getStartDate());
    }

    if (dto.getEndDate() != null) {
      if (userRenewRequest) {
        LocalDate renewalBaseDate = existing.getEndDate().isAfter(today) ? existing.getEndDate() : today;
        LocalDate renewedEndDate =
            renewalBaseDate.plusDays(existing.getSubscription().getDurationDays());

        existing.setEndDate(renewedEndDate);
        existing.setStatus(UserSubscriptionStatus.ACTIVE);
      } else {
        existing.setEndDate(dto.getEndDate());
      }
    }

    if (dto.getStatus() != null) {
      existing.setStatus(dto.getStatus());
    }

    validateUserSubscription(existing);

    UserSubscription saved = userSubscriptionRepository.save(existing);
    return modelMapper.map(saved, UserSubscriptionResponseDTO.class);
  }

  @Override
  @Transactional
  public void delete(UUID id) {
    UserSubscription existing = getEntityIncludingDeleted(id);

    if (existing.getStatus() == UserSubscriptionStatus.DELETED) {
      return;
    }

    existing.setStatus(UserSubscriptionStatus.DELETED);
    userSubscriptionRepository.save(existing);
  }

  private UserSubscription getEntity(UUID id) {
    UserSubscription entity = getEntityIncludingDeleted(id);

    if (entity.getStatus() == UserSubscriptionStatus.DELETED) {
      throw new AppException(HttpStatus.NOT_FOUND, UserSubscriptionMessage.NOT_FOUND.getMessage());
    }

    return entity;
  }

  private UserSubscription getEntityIncludingDeleted(UUID id) {
    return userSubscriptionRepository
        .findById(id)
        .orElseThrow(
            () ->
                new AppException(
                    HttpStatus.NOT_FOUND, UserSubscriptionMessage.NOT_FOUND.getMessage()));
  }

  private boolean isAdminOrManager(CustomUserDetails actor) {
    return actor != null
        && (actor.getRole() == UserRole.ADMIN || actor.getRole() == UserRole.MANAGER);
  }

  private void validateUserUpdatePermission(
      UserSubscription existing, UserSubscriptionUpdateDTO dto, CustomUserDetails actor) {
    if (actor == null) {
      throw new AppException(
          HttpStatus.UNAUTHORIZED, UserSubscriptionMessage.AUTH_REQUIRED_UPDATE.getMessage());
    }

    if (isAdminOrManager(actor)) {
      return;
    }

    if (actor.getRole() != UserRole.USER) {
      throw new AppException(
          HttpStatus.FORBIDDEN, UserSubscriptionMessage.ACCESS_DENIED_UPDATE_OTHER.getMessage());
    }

    if (!existing.getUser().getId().equals(actor.getId())) {
      throw new AppException(
          HttpStatus.FORBIDDEN, UserSubscriptionMessage.ACCESS_DENIED_UPDATE_OTHER.getMessage());
    }

    if (dto.getStartDate() != null) {
      throw new AppException(
          HttpStatus.FORBIDDEN,
          UserSubscriptionMessage.ACCESS_DENIED_UPDATE_START_DATE.getMessage());
    }

    if (dto.getStatus() != null && dto.getStatus() != UserSubscriptionStatus.CANCELED) {
      throw new AppException(
          HttpStatus.FORBIDDEN, UserSubscriptionMessage.ACCESS_DENIED_UPDATE_STATUS.getMessage());
    }

    // USER chỉ được gửi yêu cầu gia hạn bằng endDate, service sẽ tự cộng theo durationDays gói.
  }

  private void markExpiredSubscriptions(UUID userId, LocalDate today) {
    List<UserSubscription> expiredActiveSubscriptions =
        userSubscriptionRepository.findByUser_IdAndStatusAndEndDateBefore(
            userId, UserSubscriptionStatus.ACTIVE, today);

    if (expiredActiveSubscriptions == null || expiredActiveSubscriptions.isEmpty()) {
      return;
    }

    expiredActiveSubscriptions.forEach(subscription -> subscription.setStatus(UserSubscriptionStatus.EXPIRED));
    userSubscriptionRepository.saveAll(expiredActiveSubscriptions);
  }

  private void markExpiredSubscription(UserSubscription subscription, LocalDate today) {
    if (subscription.getStatus() == UserSubscriptionStatus.ACTIVE
        && subscription.getEndDate() != null
        && subscription.getEndDate().isBefore(today)) {
      subscription.setStatus(UserSubscriptionStatus.EXPIRED);
    }
  }

  private void validateUserSubscription(UserSubscription userSubscription) {
    if (userSubscription.getUser() == null) {
      throw new AppException(
          HttpStatus.BAD_REQUEST, UserSubscriptionMessage.VALIDATION_USER_REQUIRED.getMessage());
    }
    if (userSubscription.getSubscription() == null) {
      throw new AppException(
          HttpStatus.BAD_REQUEST,
          UserSubscriptionMessage.VALIDATION_SUBSCRIPTION_REQUIRED.getMessage());
    }
    if (userSubscription.getStartDate() == null) {
      throw new AppException(
          HttpStatus.BAD_REQUEST,
          UserSubscriptionMessage.VALIDATION_START_DATE_REQUIRED.getMessage());
    }
    if (userSubscription.getEndDate() == null) {
      throw new AppException(
          HttpStatus.BAD_REQUEST,
          UserSubscriptionMessage.VALIDATION_END_DATE_REQUIRED.getMessage());
    }
    if (userSubscription.getStatus() == null) {
      throw new AppException(
          HttpStatus.BAD_REQUEST, UserSubscriptionMessage.INVALID_STATUS.getMessage());
    }
    if (userSubscription.getEndDate().isBefore(userSubscription.getStartDate())) {
      throw new AppException(
          HttpStatus.BAD_REQUEST, UserSubscriptionMessage.END_DATE_BEFORE_START_DATE.getMessage());
    }
  }
}
