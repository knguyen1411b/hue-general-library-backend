package org.app.backend.modules.subscription;

import jakarta.validation.Valid;
import java.util.UUID;
import org.app.backend.modules.auth.security.CustomUserDetails;
import org.app.backend.modules.subscription.dto.SubscriptionCreateDTO;
import org.app.backend.modules.subscription.dto.SubscriptionDTO;
import org.app.backend.modules.subscription.dto.SubscriptionUpdateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SubscriptionService {
  Page<SubscriptionDTO> findAll(Pageable pageable);

  SubscriptionDTO findById(UUID id);

  void create(SubscriptionCreateDTO dto, CustomUserDetails actor);

  SubscriptionDTO findByKey(String key);

  void update(UUID id, @Valid SubscriptionUpdateDTO dto, CustomUserDetails actor);

  void delete(UUID id, CustomUserDetails actor);
}
