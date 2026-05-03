package org.app.backend.modules.configuration;

import java.util.List;
import org.app.backend.modules.configuration.exception.ConfigurationNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ConfigurationServiceImpl implements ConfigurationService {
  private final ConfigurationRepository configurationRepository;

  public ConfigurationServiceImpl(ConfigurationRepository configurationRepository) {
    this.configurationRepository = configurationRepository;
  }

  @Override
  public List<Configuration> getAllConfigurations() {
    return configurationRepository.findAll();
  }

  @Override
  public Configuration getConfigurationByKey(String key) {
    return configurationRepository
        .findByConfigKey(key)
        .orElseThrow(
            () -> new ConfigurationNotFoundException(ConfigurationMessage.CONFIG_NOT_FOUND));
  }

  @Override
  public Configuration updateConfiguration(String key, Configuration configuration) {
    Configuration existing = getConfigurationByKey(key);
    existing.setConfigValue(configuration.getConfigValue());
    existing.setDescription(configuration.getDescription());
    return configurationRepository.save(existing);
  }
}
