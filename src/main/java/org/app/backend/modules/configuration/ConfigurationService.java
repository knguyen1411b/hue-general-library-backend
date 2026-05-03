package org.app.backend.modules.configuration;

import java.util.List;

public interface ConfigurationService {
  List<Configuration> getAllConfigurations();

  Configuration getConfigurationByKey(String key);

  Configuration updateConfiguration(String key, Configuration configuration);
}
