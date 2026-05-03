package org.app.backend.modules.usersubscription;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.app.backend.common.exception.AppException;
import org.app.backend.modules.usersubscription.dto.*;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of {@link UserSubscriptionService} that manages user subscription logic. Provides
 * methods to create, update, cancel, activate, expire, and query user subscriptions.
 */
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserSubscriptionServiceImpl implements UserSubscriptionService {

  UserSubscriptionRepository userSubscriptionRepository;
  ModelMapper modelMapper;

  // ================== CRUD ==================

  @Override
  @Transactional
  public UserSubscriptionResponseDTO create(UserSubscriptionCreateDTO dto) {
    UserSubscription entity = modelMapper.map(dto, UserSubscription.class);

    validateUserSubscription(entity);

    UserSubscription saved = userSubscriptionRepository.save(entity);

    return modelMapper.map(saved, UserSubscriptionResponseDTO.class);
  }

  @Override
  @Transactional(readOnly = true)
  public UserSubscriptionResponseDTO getById(UUID id) {
    UserSubscription entity =
        userSubscriptionRepository
            .findById(id)
            .orElseThrow(
                () ->
                    new AppException(
                        HttpStatus.NOT_FOUND, UserSubscriptionMessage.NOT_FOUND.getMessage()));

    return modelMapper.map(entity, UserSubscriptionResponseDTO.class);
  }

  @Override
  @Transactional
  public UserSubscriptionResponseDTO update(UUID id, UserSubscriptionUpdateDTO dto) {
    UserSubscription existing =
        userSubscriptionRepository
            .findById(id)
            .orElseThrow(
                () ->
                    new AppException(
                        HttpStatus.NOT_FOUND, UserSubscriptionMessage.NOT_FOUND.getMessage()));

    modelMapper.map(dto, existing);

    if (existing.getEndDate().isBefore(existing.getStartDate())) {
      throw new AppException(
          HttpStatus.BAD_REQUEST, UserSubscriptionMessage.END_DATE_BEFORE_START_DATE.getMessage());
    }

    UserSubscription saved = userSubscriptionRepository.save(existing);

    return modelMapper.map(saved, UserSubscriptionResponseDTO.class);
  }

  @Override
  @Transactional
  public void delete(UUID id) {
    if (!userSubscriptionRepository.existsById(id)) {
      throw new AppException(HttpStatus.NOT_FOUND, UserSubscriptionMessage.NOT_FOUND.getMessage());
    }
    userSubscriptionRepository.deleteById(id);
  }

  // ================== PAGE ==================

  @Override
  @Transactional(readOnly = true)
  public Page<UserSubscriptionResponseDTO> getAll(Pageable pageable) {
    return userSubscriptionRepository
        .findAll(pageable)
        .map(entity -> modelMapper.map(entity, UserSubscriptionResponseDTO.class));
  }

  @Override
  @Transactional(readOnly = true)
  public Page<UserSubscriptionResponseDTO> getAll(
      Pageable pageable, UserSubscriptionStatus status, UUID userId) {

    return userSubscriptionRepository
        .findAll(pageable)
        .map(entity -> modelMapper.map(entity, UserSubscriptionResponseDTO.class));
  }

  // ================== BUSINESS ==================

  @Override
  @Transactional
  public UserSubscriptionResponseDTO activateSubscription(UUID id) {
    UserSubscription entity = getEntity(id);

    entity.setStatus(UserSubscriptionStatus.ACTIVE);

    return modelMapper.map(
        userSubscriptionRepository.save(entity), UserSubscriptionResponseDTO.class);
  }

  @Override
  @Transactional
  public UserSubscriptionResponseDTO expireSubscription(UUID id) {
    UserSubscription entity = getEntity(id);

    entity.setStatus(UserSubscriptionStatus.EXPIRED);

    return modelMapper.map(
        userSubscriptionRepository.save(entity), UserSubscriptionResponseDTO.class);
  }

  @Override
  @Transactional
  public UserSubscriptionResponseDTO cancelSubscription(UUID id) {
    UserSubscription entity = getEntity(id);

    entity.setStatus(UserSubscriptionStatus.CANCELED);

    return modelMapper.map(
        userSubscriptionRepository.save(entity), UserSubscriptionResponseDTO.class);
  }

  @Override
  @Transactional
  public UserSubscriptionResponseDTO renewSubscription(UUID id, LocalDate start, LocalDate end) {

    if (end.isBefore(start)) {
      throw new AppException(
          HttpStatus.BAD_REQUEST, UserSubscriptionMessage.END_DATE_BEFORE_START_DATE.getMessage());
    }

    UserSubscription entity = getEntity(id);

    entity.setStartDate(start);
    entity.setEndDate(end);
    entity.setStatus(UserSubscriptionStatus.ACTIVE);

    return modelMapper.map(
        userSubscriptionRepository.save(entity), UserSubscriptionResponseDTO.class);
  }

  // ================== QUERY ==================

  @Override
  @Transactional(readOnly = true)
  public List<UserSubscriptionResponseDTO> getByUserId(UUID userId) {
    return userSubscriptionRepository.findByUserId(userId).stream()
        .map(e -> modelMapper.map(e, UserSubscriptionResponseDTO.class))
        .toList();
  }

  @Override
  @Transactional(readOnly = true)
  public List<UserSubscriptionResponseDTO> getBySubscriptionId(UUID subscriptionId) {
    return userSubscriptionRepository.findBySubscriptionId(subscriptionId).stream()
        .map(e -> modelMapper.map(e, UserSubscriptionResponseDTO.class))
        .toList();
  }

  @Override
  @Transactional(readOnly = true)
  public List<UserSubscriptionResponseDTO> getByStatus(UserSubscriptionStatus status) {
    return userSubscriptionRepository.findByStatus(status).stream()
        .map(e -> modelMapper.map(e, UserSubscriptionResponseDTO.class))
        .toList();
  }

  @Override
  @Transactional(readOnly = true)
  public List<UserSubscriptionResponseDTO> getActiveSubscriptionsByUser(UUID userId) {
    return userSubscriptionRepository
        .findByUserIdAndStatus(userId, UserSubscriptionStatus.ACTIVE)
        .stream()
        .filter(us -> !us.getEndDate().isBefore(LocalDate.now()))
        .map(e -> modelMapper.map(e, UserSubscriptionResponseDTO.class))
        .toList();
  }

  // ================== STATS ==================

  @Override
  @Transactional(readOnly = true)
  public long countByStatus(UserSubscriptionStatus status) {
    return userSubscriptionRepository.countByStatus(status);
  }

  @Override
  @Transactional(readOnly = true)
  public List<UserSubscriptionResponseDTO> getAll() {
    return userSubscriptionRepository.findAll().stream()
        .map(e -> modelMapper.map(e, UserSubscriptionResponseDTO.class))
        .toList();
  }

  // ================== PRIVATE ==================

  private UserSubscription getEntity(UUID id) {
    return userSubscriptionRepository
        .findById(id)
        .orElseThrow(
            () ->
                new AppException(
                    HttpStatus.NOT_FOUND, UserSubscriptionMessage.NOT_FOUND.getMessage()));
  }

  // private void validateUserSubscription(UserSubscription entity) {
  // if (entity.getUser() == null) {
  // throw new UserSubscriptionValidationException("User is required");
  // }
  // if (entity.getSubscription() == null) {
  // throw new UserSubscriptionValidationException("Subscription is required");
  // }
  // if (entity.getStartDate() == null || entity.getEndDate() == null) {
  // throw new UserSubscriptionValidationException("Date is required");
  // }
  // }

  /**
   * Retrieves all expired subscriptions across all users.
   *
   * @return list of expired subscriptions
   */

  /**
   * Retrieves all canceled subscriptions across all users.
   *
   * @return list of canceled subscriptions
   */

  /**
   * Determines whether a user can subscribe to a given subscription plan. A user can subscribe only
   * if they do not already have an active subscription.
   *
   * @param userId the ID of the user
   * @param subscriptionId the ID of the subscription plan
   * @return true if the user can subscribe, false otherwise
   */

  /**
   * Checks if a subscription is currently active.
   *
   * @param userSubscriptionId the ID of the subscription
   * @return true if the subscription is active and not expired
   */

  /**
   * Checks if a subscription is expired. An active subscription that has passed its end date is
   * considered expired.
   *
   * @param userSubscriptionId the ID of the subscription
   * @return true if the subscription is expired
   */

  /**
   * Checks if a subscription is canceled.
   *
   * @param userSubscriptionId the ID of the subscription
   * @return true if the subscription status is canceled
   */

  /**
   * Counts the number of active subscriptions.
   *
   * @return the count of active subscriptions
   */

  /**
   * Counts the number of expired subscriptions.
   *
   * @return the count of expired subscriptions
   */

  /**
   * Counts the number of canceled subscriptions.
   *
   * @return the count of canceled subscriptions
   */

  /**
   * Counts the number of subscriptions for a specific user.
   *
   * @param userId the ID of the user
   * @return the count of subscriptions for the user
   */

  /**
   * Counts the number of subscriptions with a given status.
   *
   * @param status the status to count
   * @return the count of subscriptions with that status
   */
  /**
   * Validates the fields of a user subscription to ensure they meet business rules. This includes
   * checking for required fields and that the end date is not before the start date.
   *
   * @param userSubscription the subscription to validate
   * @throws UserSubscriptionValidationException if validation fails
   */
  private void validateUserSubscription(UserSubscription userSubscription) {
    if (userSubscription.getUser() == null) {
      throw new AppException(
          HttpStatus.BAD_REQUEST, UserSubscriptionMessage.VALIDATION_USER_REQUIRED.getMessage());
    }
    // Validate that a subscription is provided
    if (userSubscription.getSubscription() == null) {
      throw new AppException(
          HttpStatus.BAD_REQUEST,
          UserSubscriptionMessage.VALIDATION_SUBSCRIPTION_REQUIRED.getMessage());
    }
    // Validate that a start date is provided
    if (userSubscription.getStartDate() == null) {
      throw new AppException(
          HttpStatus.BAD_REQUEST,
          UserSubscriptionMessage.VALIDATION_START_DATE_REQUIRED.getMessage());
    }
    // Validate that an end date is provided
    if (userSubscription.getEndDate() == null) {
      throw new AppException(
          HttpStatus.BAD_REQUEST,
          UserSubscriptionMessage.VALIDATION_END_DATE_REQUIRED.getMessage());
    }
    // Validate that the end date is not before the start date
    if (userSubscription.getEndDate().isBefore(userSubscription.getStartDate())) {
      throw new AppException(
          HttpStatus.BAD_REQUEST, UserSubscriptionMessage.END_DATE_BEFORE_START_DATE.getMessage());
    }
  }

  @Override
  @Transactional
  public UserSubscriptionResponseDTO create(UserSubscription userSubscription) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'create'");
  }

  @Override
  @Transactional
  public UserSubscriptionResponseDTO update(UUID id, UserSubscription userSubscription) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'update'");
  }
}
