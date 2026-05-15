package org.app.backend.modules.usersubscription;

import java.util.List;
import java.util.UUID;
import org.app.backend.modules.auth.security.CustomUserDetails;
import org.app.backend.modules.usersubscription.dto.UserSubscriptionCreateDTO;
import org.app.backend.modules.usersubscription.dto.UserSubscriptionResponseDTO;
import org.app.backend.modules.usersubscription.dto.UserSubscriptionUpdateDTO;

public interface UserSubscriptionService {

  UserSubscriptionResponseDTO create(UserSubscriptionCreateDTO dto);

  UserSubscriptionResponseDTO getById(UUID id);

  List<UserSubscriptionResponseDTO> getAll();

  List<UserSubscriptionResponseDTO> getByUserId(UUID userId);

  UserSubscriptionResponseDTO update(UUID id, UserSubscriptionUpdateDTO dto, CustomUserDetails actor);

  void delete(UUID id);
}
