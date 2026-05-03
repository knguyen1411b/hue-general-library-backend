package org.app.backend.modules.configuration;

import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.app.backend.common.exception.AppException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ConfigurationServiceImpl implements ConfigurationService {
  ConfigurationRepository configurationRepository;

  @Override
  @Transactional(readOnly = true)
  public List<Configuration> getAllConfigurations() {
    return configurationRepository.findAll();
  }

  @Override
  @Transactional(readOnly = true)
  public Configuration getConfigurationByKey(String key) {
    return configurationRepository
        .findByConfigKey(key)
        .orElseThrow(
            () ->
                new AppException(
                    HttpStatus.NOT_FOUND, ConfigurationMessage.CONFIG_NOT_FOUND.getMessage()));
  }

  @Override
  @Transactional
  public Configuration updateConfiguration(String key, Configuration configuration) {
    Configuration existing = getConfigurationByKey(key);
    existing.setConfigValue(configuration.getConfigValue());
    existing.setDescription(configuration.getDescription());
    return configurationRepository.save(existing);
  }
}
