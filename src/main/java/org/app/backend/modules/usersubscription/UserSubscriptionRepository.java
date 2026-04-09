package org.app.backend.modules.usersubscription;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserSubscriptionRepository extends JpaRepository<UserSubscription, UUID> {
  boolean existsByUserIdAndSubscriptionId(UUID userId, UUID subscriptionId);

  java.util.List<UserSubscription> findByUserId(UUID userId);

  java.util.List<UserSubscription> findBySubscriptionId(UUID subscriptionId);
}
