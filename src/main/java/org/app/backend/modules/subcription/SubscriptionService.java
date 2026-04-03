package org.app.backend.modules.subcription;

import java.util.UUID;
import org.app.backend.modules.auth.security.CustomUserDetails;
import org.app.backend.modules.subcription.dto.SubscriptionCreateDTO;
import org.app.backend.modules.subcription.dto.SubscriptionDTO;
import org.app.backend.modules.subcription.dto.SubscriptionUpdateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SubscriptionService {
    Page<SubscriptionDTO> findAll(Pageable pageable);

    SubscriptionDTO findById(UUID id);

    SubscriptionDTO findByKey(String key);

    void create(SubscriptionCreateDTO dto, CustomUserDetails actor);

    void update(UUID id, SubscriptionUpdateDTO dto, CustomUserDetails actor);

    void delete(UUID id, CustomUserDetails actor);
}
