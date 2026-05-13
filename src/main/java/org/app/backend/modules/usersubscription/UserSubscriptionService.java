package org.app.backend.modules.usersubscription;

import java.util.List;
import java.util.UUID;
import org.app.backend.modules.auth.security.CustomUserDetails;
import org.app.backend.modules.usersubscription.dto.UserSubscriptionCreateDTO;
import org.app.backend.modules.usersubscription.dto.UserSubscriptionFilterDTO;
import org.app.backend.modules.usersubscription.dto.UserSubscriptionResponseDTO;

public interface UserSubscriptionService {

  UserSubscriptionResponseDTO create(UserSubscriptionCreateDTO dto);

  UserSubscriptionResponseDTO getById(UUID id, CustomUserDetails actor);

  List<UserSubscriptionResponseDTO> getAll(UserSubscriptionFilterDTO filter, CustomUserDetails actor);

  UserSubscriptionResponseDTO update(UUID id, UserSubscriptionAction action, CustomUserDetails actor);

  void delete(UUID id);
}