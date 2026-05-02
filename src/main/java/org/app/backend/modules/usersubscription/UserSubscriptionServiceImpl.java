package org.app.backend.modules.usersubscription;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.app.backend.modules.auth.security.CustomUserDetails;
import org.app.backend.modules.usersubscription.exception.UserSubscriptionNotFoundException;
import org.app.backend.modules.usersubscription.exception.UserSubscriptionValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * Implementation of {@link UserSubscriptionService} that manages user subscription logic. Provides
 * methods to create, update, cancel, activate, expire, and query user subscriptions.
 */
@Service
public class UserSubscriptionServiceImpl implements UserSubscriptionService {

    private static final Logger logger = LoggerFactory.getLogger(
        UserSubscriptionServiceImpl.class
    );

    @Autowired
    private UserSubscriptionRepository userSubscriptionRepository;

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
            userSubscription.getUser() != null
                ? userSubscription.getUser().getId()
                : null,
            userSubscription.getSubscription() != null
                ? userSubscription.getSubscription().getId()
                : null
        );

        // Validate required fields
        if (userSubscription.getUser() == null) {
            throw new UserSubscriptionValidationException("User is required");
        }
        if (userSubscription.getSubscription() == null) {
            throw new UserSubscriptionValidationException(
                "Subscription is required"
            );
        }
        if (userSubscription.getStartDate() == null) {
            throw new UserSubscriptionValidationException(
                "Start date is required"
            );
        }
        if (userSubscription.getEndDate() == null) {
            throw new UserSubscriptionValidationException(
                "End date is required"
            );
        }
        if (userSubscription.getMaxBooks() == null) {
            throw new UserSubscriptionValidationException(
                "Max books is required"
            );
        }
        if (userSubscription.getPrice() == null) {
            throw new UserSubscriptionValidationException("Price is required");
        }

        // Validate date range
        if (
            userSubscription
                .getEndDate()
                .isBefore(userSubscription.getStartDate())
        ) {
            throw new UserSubscriptionValidationException(
                "End date must be after or equal to start date"
            );
        }

        // Check for existing active subscription for this user
        boolean hasActive = userSubscriptionRepository.existsByUserIdAndStatus(
            userSubscription.getUser().getId(),
            UserSubscriptionStatus.ACTIVE
        );
        if (hasActive) {
            throw new UserSubscriptionValidationException(
                "User already has an active subscription"
            );
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
            .orElseThrow(() ->
                new UserSubscriptionNotFoundException(
                    UserSubscriptionMessage.NOT_FOUND
                )
            );
    }

    @Override
    public UserSubscription update(UUID id, UserSubscription userSubscription) {
        logger.info(UserSubscriptionMessage.LOG_UPDATING, id);

        UserSubscription existing = getById(id);

        // Don't allow updating user or subscription
        if (
            userSubscription.getUser() != null &&
            !userSubscription
                .getUser()
                .getId()
                .equals(existing.getUser().getId())
        ) {
            throw new UserSubscriptionValidationException(
                "Cannot change user for existing subscription"
            );
        }

        if (
            userSubscription.getSubscription() != null &&
            !userSubscription
                .getSubscription()
                .getId()
                .equals(existing.getSubscription().getId())
        ) {
            throw new UserSubscriptionValidationException(
                "Cannot change subscription for existing subscription"
            );
        }

        // Update fields
        LocalDate newStartDate = userSubscription.getStartDate();
        LocalDate newEndDate = userSubscription.getEndDate();

        if (newStartDate != null) {
            existing.setStartDate(newStartDate);
        }
        if (newEndDate != null) {
            existing.setEndDate(newEndDate);
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

        // Validate date range after updates
        LocalDate finalStartDate = existing.getStartDate();
        LocalDate finalEndDate = existing.getEndDate();
        if (finalEndDate.isBefore(finalStartDate)) {
            throw new UserSubscriptionValidationException(
                "End date must be after or equal to start date"
            );
        }

        return userSubscriptionRepository.save(existing);
    }

    @Override
    public void delete(UUID id) {
        logger.info(UserSubscriptionMessage.LOG_DELETING, id);
        if (!userSubscriptionRepository.existsById(id)) {
            throw new UserSubscriptionNotFoundException(
                UserSubscriptionMessage.NOT_FOUND
            );
        }
        userSubscriptionRepository.deleteById(id);
    }

    @Override
    public Page<UserSubscription> getAll(Pageable pageable) {
        logger.debug(UserSubscriptionMessage.LOG_LISTING);
        return userSubscriptionRepository.findAll(pageable);
    }

    @Override
    public List<UserSubscription> getAll() {
        logger.debug(UserSubscriptionMessage.LOG_LISTING);
        return userSubscriptionRepository.findAll();
    }
}
