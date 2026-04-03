package org.app.backend.modules.subcription;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SubscriptionRepository
    extends JpaRepository<Subscription, UUID>, JpaSpecificationExecutor<Subscription> {
  boolean existsByName(String name);

  boolean existsByKey(String key);

  Optional<Subscription> findByKey(String key);

  Optional<Subscription> findByName(String name);
}
