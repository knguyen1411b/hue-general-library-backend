package org.app.backend.modules.usersubscription;

import java.util.List;
import java.util.UUID;
import org.app.backend.modules.usersubscription.dto.UserSubscriptionCreateDTO;
import org.app.backend.modules.usersubscription.dto.UserSubscriptionResponseDTO;

public interface UserSubscriptionService {

  UserSubscriptionResponseDTO create(UserSubscriptionCreateDTO dto);

  UserSubscriptionResponseDTO getById(UUID id);

  List<UserSubscriptionResponseDTO> getAll();

  UserSubscriptionResponseDTO update(UUID id, UserSubscription userSubscription);

  void delete(UUID id);
}