package org.app.backend.modules.configuration;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfigurationRepository extends JpaRepository<Configuration, Long> {
  Optional<Configuration> findByConfigKey(String configKey);
}
