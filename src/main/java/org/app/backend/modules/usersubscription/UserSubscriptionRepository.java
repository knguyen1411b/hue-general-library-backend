package org.app.backend.modules.usersubscription;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserSubscriptionRepository extends JpaRepository<UserSubscription, UUID> {
  boolean existsByUserIdAndSubscriptionId(UUID userId, UUID subscriptionId);

  java.util.List<UserSubscription> findByUserId(UUID userId);

  java.util.List<UserSubscription> findBySubscriptionId(UUID subscriptionId);

  @Query(
      "SELECT us FROM UserSubscription us WHERE us.user.id = :userId AND us.endDate > CURRENT_TIMESTAMP ORDER BY us.endDate DESC LIMIT 1")
  Optional<UserSubscription> findActiveSubscriptionByUserId(@Param("userId") UUID userId);
}
