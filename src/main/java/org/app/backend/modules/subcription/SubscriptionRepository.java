package org.app.backend.modules.subcription;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriptionRepository
    extends JpaRepository<Subscription, UUID>
{
    boolean existsByName(String name);

    boolean existsByKey(String key);

    Optional<Subscription> findByKey(String key);

    Optional<Subscription> findByName(String name);
}
