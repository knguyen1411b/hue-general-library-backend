package org.app.backend.modules.usersubscription;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserSubscriptionRepository extends JpaRepository<UserSubscription, UUID> {

  boolean existsByUserIdAndStatus(UUID userId, UserSubscriptionStatus status);

  boolean existsBySubscriptionIdAndStatus(UUID subscriptionId, UserSubscriptionStatus status);

  List<UserSubscription> findByUserId(UUID userId);

  List<UserSubscription> findBySubscriptionId(UUID subscriptionId);

  List<UserSubscription> findByStatus(UserSubscriptionStatus status);

  List<UserSubscription> findByUserIdAndStatus(UUID userId, UserSubscriptionStatus status);

  long countByStatus(UserSubscriptionStatus status);

  Optional<UserSubscription> findTopByUserIdAndEndDateAfterOrderByEndDateDesc(
      UUID userId, LocalDate now);

  default Optional<UserSubscription> findActiveSubscriptionByUserId(UUID userId) {
    return findTopByUserIdAndEndDateAfterOrderByEndDateDesc(userId, LocalDate.now())
        .filter(sub -> sub.getStatus() == UserSubscriptionStatus.ACTIVE);
  }
}
