package org.app.backend.modules.usersubscription;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserSubscriptionService {
  // CRUD operations
  UserSubscription create(UserSubscription userSubscription);

  UserSubscription getById(UUID id);

  UserSubscription update(UUID id, UserSubscription userSubscription);

  void delete(UUID id);

  // Pageable methods
  Page<UserSubscription> getAll(Pageable pageable);

  Page<UserSubscription> getAll(Pageable pageable, UserSubscriptionStatus status, UUID userId);

  // Business operations
  UserSubscription activateSubscription(UUID userSubscriptionId);

  UserSubscription expireSubscription(UUID userSubscriptionId);

  UserSubscription cancelSubscription(UUID userSubscriptionId);

  UserSubscription renewSubscription(
      UUID userSubscriptionId, LocalDate newStartDate, LocalDate newEndDate);

  // Query methods
  List<UserSubscription> getByUserId(UUID userId);

  List<UserSubscription> getBySubscriptionId(UUID subscriptionId);

  List<UserSubscription> getByStatus(UserSubscriptionStatus status);

  List<UserSubscription> getActiveSubscriptionsByUser(UUID userId);

  // Validation methods

  // Statistics

  long countByStatus(UserSubscriptionStatus status);
}
