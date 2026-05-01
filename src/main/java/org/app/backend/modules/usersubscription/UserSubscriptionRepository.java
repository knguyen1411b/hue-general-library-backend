package org.app.backend.modules.usersubscription;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserSubscriptionRepository extends JpaRepository<UserSubscription, UUID> {
  boolean existsByUserIdAndStatus(UUID userId, UserSubscriptionStatus status);

  boolean existsBySubscriptionIdAndStatus(UUID subscriptionId, UserSubscriptionStatus status);
}
