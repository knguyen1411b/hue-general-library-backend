package org.app.backend.modules.usersubscription;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.app.backend.modules.subscription.SubscriptionRepository;
import org.app.backend.modules.user.UserRepository;
import org.app.backend.modules.usersubscription.exception.UserSubscriptionNotFoundException;
import org.app.backend.modules.usersubscription.exception.UserSubscriptionValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Implementation of {@link UserSubscriptionService} that manages user subscription logic. Provides
 * methods to create, update, cancel, activate, expire, and query user subscriptions.
 */
@Service
public class UserSubscriptionServiceImpl implements UserSubscriptionService {

  private static final Logger logger = LoggerFactory.getLogger(UserSubscriptionServiceImpl.class);

  @Autowired private UserSubscriptionRepository userSubscriptionRepository;

  @Autowired private UserRepository userRepository;

  @Autowired private SubscriptionRepository subscriptionRepository;

  /**
   * Creates a new user subscription after validating the input and ensuring the user does not
   * already have an active subscription.
   *
   * @param userSubscription the subscription to create
   * @return the saved user subscription
   */
  @Override
  public UserSubscription create(UserSubscription userSubscription) {
    logger.info(
        UserSubscriptionMessage.LOG_CREATING,
        userSubscription.getUser() != null ? userSubscription.getUser().getId() : null,
        userSubscription.getSubscription() != null
            ? userSubscription.getSubscription().getId()
            : null);

    validateUserSubscription(userSubscription);

    // Check if subscription already exists for this user
    // if (
    // userSubscriptionRepository.existsByUserIdAndSubscriptionId(
    // userSubscription.getUser().getId(),
    // userSubscription.getSubscription().getId()
    // )
    // ) {
    // throw new UserSubscriptionAlreadyExistsException(
    // UserSubscriptionMessage.ALREADY_EXISTS
    // );
    // }

    // // Check if the user already has an active subscription
    List<UserSubscription> activeSubscriptions =
        getActiveSubscriptionsByUser(userSubscription.getUser().getId());
    if (!activeSubscriptions.isEmpty()) {
      throw new UserSubscriptionValidationException("User already has an active subscription");
    }

    // Set default status if not provided
    if (userSubscription.getStatus() == null) {
      userSubscription.setStatus(UserSubscriptionStatus.ACTIVE);
    }

    return userSubscriptionRepository.save(userSubscription);
  }

  @Override
  public UserSubscription getById(UUID id) {
    logger.debug(UserSubscriptionMessage.LOG_FOUND, id);
    return userSubscriptionRepository
        .findById(id)
        .orElseThrow(
            () -> new UserSubscriptionNotFoundException(UserSubscriptionMessage.NOT_FOUND));
  }

  @Override
  public UserSubscription update(UUID id, UserSubscription userSubscription) {
    logger.info(UserSubscriptionMessage.LOG_UPDATING, id);

    UserSubscription existing = getById(id);

    // Don't allow updating user or subscription
    if (userSubscription.getUser() != null
        && !userSubscription.getUser().getId().equals(existing.getUser().getId())) {
      throw new UserSubscriptionValidationException("Cannot change user for existing subscription");
    }

    if (userSubscription.getSubscription() != null
        && !userSubscription.getSubscription().getId().equals(existing.getSubscription().getId())) {
      throw new UserSubscriptionValidationException(
          "Cannot change subscription for existing subscription");
    }

    // Update fields
    if (userSubscription.getStartDate() != null) {
      existing.setStartDate(userSubscription.getStartDate());
    }
    if (userSubscription.getEndDate() != null) {
      existing.setEndDate(userSubscription.getEndDate());
    }
    if (userSubscription.getStatus() != null) {
      existing.setStatus(userSubscription.getStatus());
    }
    if (userSubscription.getMaxBooks() != null) {
      existing.setMaxBooks(userSubscription.getMaxBooks());
    }
    if (userSubscription.getPrice() != null) {
      existing.setPrice(userSubscription.getPrice());
    }

    return userSubscriptionRepository.save(existing);
  }

  @Override
  public void delete(UUID id) {
    logger.info(UserSubscriptionMessage.LOG_DELETING, id);
    if (!userSubscriptionRepository.existsById(id)) {
      throw new UserSubscriptionNotFoundException(UserSubscriptionMessage.NOT_FOUND);
    }
    userSubscriptionRepository.deleteById(id);
  }

  @Override
  public Page<UserSubscription> getAll(Pageable pageable) {
    logger.debug(UserSubscriptionMessage.LOG_LISTING);
    return userSubscriptionRepository.findAll(pageable);
  }

  @Override
  public Page<UserSubscription> getAll(
      Pageable pageable, UserSubscriptionStatus status, UUID userId) {
    logger.debug("Getting user subscriptions with filters");
    // Simple implementation - can be enhanced with Specification
    if (status != null) {
      return userSubscriptionRepository.findAll(
          pageable); // Simplified - need Specification for real filtering
    }
    if (userId != null) {
      return userSubscriptionRepository.findAll(
          pageable); // Simplified - need Specification for real filtering
    }
    return userSubscriptionRepository.findAll(pageable);
  }

  /**
   * Activates a subscription that is currently in a non‑active state.
   *
   * @param userSubscriptionId the ID of the subscription to activate
   * @return the activated subscription
   */
  @Override
  public UserSubscription activateSubscription(UUID userSubscriptionId) {
    logger.debug(UserSubscriptionMessage.LOG_CHECKING_STATUS, userSubscriptionId);
    UserSubscription userSubscription = getById(userSubscriptionId);

    if (userSubscription.getStatus() == UserSubscriptionStatus.ACTIVE) {
      throw new UserSubscriptionValidationException("Subscription is already active");
    }

    userSubscription.setStatus(UserSubscriptionStatus.ACTIVE);
    return userSubscriptionRepository.save(userSubscription);
  }

  /**
   * Expires a subscription that is currently active.
   *
   * @param userSubscriptionId the ID of the subscription to expire
   * @return the expired subscription
   */
  @Override
  public UserSubscription expireSubscription(UUID userSubscriptionId) {
    logger.debug(UserSubscriptionMessage.LOG_CHECKING_STATUS, userSubscriptionId);
    UserSubscription userSubscription = getById(userSubscriptionId);

    if (userSubscription.getStatus() == UserSubscriptionStatus.EXPIRED) {
      throw new UserSubscriptionValidationException("Subscription is already expired");
    }

    userSubscription.setStatus(UserSubscriptionStatus.EXPIRED);
    return userSubscriptionRepository.save(userSubscription);
  }

  /**
   * Cancels a subscription that is currently active or expired.
   *
   * @param userSubscriptionId the ID of the subscription to cancel
   * @return the canceled subscription
   */
  @Override
  public UserSubscription cancelSubscription(UUID userSubscriptionId) {
    logger.debug(UserSubscriptionMessage.LOG_CHECKING_STATUS, userSubscriptionId);
    UserSubscription userSubscription = getById(userSubscriptionId);

    if (userSubscription.getStatus() == UserSubscriptionStatus.CANCELED) {
      throw new UserSubscriptionValidationException("Subscription is already canceled");
    }

    userSubscription.setStatus(UserSubscriptionStatus.CANCELED);
    return userSubscriptionRepository.save(userSubscription);
  }

  /**
   * Renews a subscription, updating its start and end dates and setting it to active.
   *
   * @param userSubscriptionId the ID of the subscription to renew
   * @param newStartDate the new start date
   * @param newEndDate the new end date
   * @return the renewed subscription
   */
  @Override
  public UserSubscription renewSubscription(
      UUID userSubscriptionId, LocalDate newStartDate, LocalDate newEndDate) {
    logger.debug(UserSubscriptionMessage.LOG_CHECKING_STATUS, userSubscriptionId);
    UserSubscription userSubscription = getById(userSubscriptionId);

    if (newEndDate.isBefore(newStartDate)) {
      throw new UserSubscriptionValidationException(
          UserSubscriptionMessage.END_DATE_BEFORE_START_DATE);
    }

    userSubscription.setStartDate(newStartDate);
    userSubscription.setEndDate(newEndDate);
    userSubscription.setStatus(UserSubscriptionStatus.ACTIVE);

    return userSubscriptionRepository.save(userSubscription);
  }

  /**
   * Retrieves all subscriptions belonging to a specific user.
   *
   * @param userId the ID of the user
   * @return list of subscriptions for the user
   */
  @Override
  public List<UserSubscription> getByUserId(UUID userId) {
    logger.debug("Getting subscriptions for user: {}", userId);
    return userSubscriptionRepository.findByUserId(userId);
  }

  /**
   * Retrieves all subscriptions belonging to a specific subscription plan.
   *
   * @param subscriptionId the ID of the subscription plan
   * @return list of subscriptions for the plan
   */
  @Override
  public List<UserSubscription> getBySubscriptionId(UUID subscriptionId) {
    logger.debug("Getting subscriptions for subscription plan: {}", subscriptionId);
    return userSubscriptionRepository.findBySubscriptionId(subscriptionId);
  }

  /**
   * Retrieves subscriptions filtered by their status.
   *
   * @param status the status to filter by
   * @return list of subscriptions with the given status
   */
  @Override
  public List<UserSubscription> getByStatus(UserSubscriptionStatus status) {
    logger.debug("Getting subscriptions with status: {}", status);
    return userSubscriptionRepository.findAll().stream()
        .filter(us -> us.getStatus() == status)
        .collect(Collectors.toList());
  }

  /**
   * Retrieves active subscriptions for a given user that have not yet expired.
   *
   * @param userId the ID of the user
   * @return list of active, non‑expired subscriptions for the user
   */
  @Override
  public List<UserSubscription> getActiveSubscriptionsByUser(UUID userId) {
    logger.debug("Getting active subscriptions for user: {}", userId);
    LocalDate today = LocalDate.now();
    return userSubscriptionRepository.findByUserId(userId).stream()
        .filter(us -> us.getStatus() == UserSubscriptionStatus.ACTIVE)
        .filter(us -> !us.getEndDate().isBefore(today))
        .collect(Collectors.toList());
  }

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
  @Override
  public long countByStatus(UserSubscriptionStatus status) {
    return userSubscriptionRepository.findAll().stream()
        .filter(us -> us.getStatus() == status)
        .count();
  }

  /**
   * Validates the fields of a user subscription to ensure they meet business rules. This includes
   * checking for required fields and that the end date is not before the start date.
   *
   * @param userSubscription the subscription to validate
   * @throws UserSubscriptionValidationException if validation fails
   */
  private void validateUserSubscription(UserSubscription userSubscription) {
    if (userSubscription.getUser() == null) {
      throw new UserSubscriptionValidationException(
          UserSubscriptionMessage.VALIDATION_USER_REQUIRED);
    }
    // Validate that a subscription is provided
    if (userSubscription.getSubscription() == null) {
      throw new UserSubscriptionValidationException(
          UserSubscriptionMessage.VALIDATION_SUBSCRIPTION_REQUIRED);
    }
    // Validate that a start date is provided
    if (userSubscription.getStartDate() == null) {
      throw new UserSubscriptionValidationException(
          UserSubscriptionMessage.VALIDATION_START_DATE_REQUIRED);
    }
    // Validate that an end date is provided
    if (userSubscription.getEndDate() == null) {
      throw new UserSubscriptionValidationException(
          UserSubscriptionMessage.VALIDATION_END_DATE_REQUIRED);
    }
    // Validate that the end date is not before the start date
    if (userSubscription.getEndDate().isBefore(userSubscription.getStartDate())) {
      throw new UserSubscriptionValidationException(
          UserSubscriptionMessage.END_DATE_BEFORE_START_DATE);
    }
  }
}
