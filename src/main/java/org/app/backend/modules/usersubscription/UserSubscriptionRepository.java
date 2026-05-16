package org.app.backend.modules.usersubscription;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserSubscriptionRepository extends JpaRepository<UserSubscription, UUID> {

  boolean existsByUser_IdAndStatus(UUID userId, UserSubscriptionStatus status);

  boolean existsBySubscription_IdAndStatus(UUID subscriptionId, UserSubscriptionStatus status);

  List<UserSubscription> findByUser_Id(UUID userId);

  List<UserSubscription> findByStatusNot(UserSubscriptionStatus status);

  List<UserSubscription> findByUser_IdAndStatusNot(UUID userId, UserSubscriptionStatus status);

  List<UserSubscription> findBySubscription_Id(UUID subscriptionId);

  List<UserSubscription> findByStatus(UserSubscriptionStatus status);

  List<UserSubscription> findByUser_IdAndStatus(UUID userId, UserSubscriptionStatus status);

  long countByStatus(UserSubscriptionStatus status);

  Optional<UserSubscription> findTopByUser_IdAndEndDateAfterOrderByEndDateDesc(
      UUID userId, LocalDate now);

  default Optional<UserSubscription> findActiveSubscriptionByUserId(UUID userId) {
    return findTopByUser_IdAndEndDateAfterOrderByEndDateDesc(userId, LocalDate.now())
        .filter(sub -> sub.getStatus() == UserSubscriptionStatus.ACTIVE);
  }
}
