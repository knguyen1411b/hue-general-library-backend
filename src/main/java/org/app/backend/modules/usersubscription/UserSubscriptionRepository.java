package org.app.backend.modules.usersubscription;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserSubscriptionRepository extends JpaRepository<UserSubscription, UUID> {

  boolean existsByUser_IdAndStatus(UUID userId, UserSubscriptionStatus status);

  boolean existsBySubscription_IdAndStatus(UUID subscriptionId, UserSubscriptionStatus status);

  List<UserSubscription> findByUser_Id(UUID userId);

  @EntityGraph(attributePaths = {"user", "subscription"})
  List<UserSubscription> findByStatusNot(UserSubscriptionStatus status);

  @EntityGraph(attributePaths = {"user", "subscription"})
  List<UserSubscription> findByUser_IdAndStatusNot(UUID userId, UserSubscriptionStatus status);

  List<UserSubscription> findBySubscription_Id(UUID subscriptionId);

  List<UserSubscription> findByStatus(UserSubscriptionStatus status);

  List<UserSubscription> findByUser_IdAndStatus(UUID userId, UserSubscriptionStatus status);

  List<UserSubscription> findByUser_IdAndStatusAndEndDateBefore(
      UUID userId, UserSubscriptionStatus status, LocalDate date);

  @EntityGraph(attributePaths = {"user", "subscription"})
  Optional<UserSubscription> findTopByUser_IdAndStatusAndEndDateGreaterThanEqualOrderByEndDateDesc(
      UUID userId, UserSubscriptionStatus status, LocalDate date);

  long countByStatus(UserSubscriptionStatus status);

  default Optional<UserSubscription> findActiveSubscriptionByUserId(UUID userId) {
    return findTopByUser_IdAndStatusAndEndDateGreaterThanEqualOrderByEndDateDesc(
        userId, UserSubscriptionStatus.ACTIVE, LocalDate.now());
  }
}
