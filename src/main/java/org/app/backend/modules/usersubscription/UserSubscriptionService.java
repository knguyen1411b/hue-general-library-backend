package org.app.backend.modules.usersubscription;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.app.backend.modules.usersubscription.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserSubscriptionService {

  // CRUD
  UserSubscriptionResponseDTO create(UserSubscription userSubscription);

  UserSubscriptionResponseDTO getById(UUID id);

  UserSubscriptionResponseDTO update(UUID id, UserSubscription userSubscription);

  void delete(UUID id);

  // Pageable
  Page<UserSubscriptionResponseDTO> getAll(Pageable pageable);

  Page<UserSubscriptionResponseDTO> getAll(
      Pageable pageable, UserSubscriptionStatus status, UUID userId);

  // Business
  UserSubscriptionResponseDTO activateSubscription(UUID userSubscriptionId);

  UserSubscriptionResponseDTO expireSubscription(UUID userSubscriptionId);

  UserSubscriptionResponseDTO cancelSubscription(UUID userSubscriptionId);

  UserSubscriptionResponseDTO renewSubscription(
      UUID userSubscriptionId, LocalDate newStartDate, LocalDate newEndDate);

  // Query
  List<UserSubscriptionResponseDTO> getByUserId(UUID userId);

  List<UserSubscriptionResponseDTO> getBySubscriptionId(UUID subscriptionId);

  List<UserSubscriptionResponseDTO> getByStatus(UserSubscriptionStatus status);

  List<UserSubscriptionResponseDTO> getActiveSubscriptionsByUser(UUID userId);

  // Stats
  long countByStatus(UserSubscriptionStatus status);

  List<UserSubscriptionResponseDTO> getAll();

  UserSubscriptionResponseDTO create(UserSubscriptionCreateDTO dto);

  UserSubscriptionResponseDTO update(UUID id, UserSubscriptionUpdateDTO dto);
}
