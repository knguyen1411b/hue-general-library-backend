package org.app.backend.modules.configuration;

import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;
import org.app.backend.modules.configuration.dto.ConfigurationDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/configurations")
@Validated
public class ConfigurationV1Controller {
  private static final Logger logger = LoggerFactory.getLogger(ConfigurationV1Controller.class);
  private final ConfigurationService configurationService;

  public ConfigurationV1Controller(ConfigurationService configurationService) {
    this.configurationService = configurationService;
  }

  @GetMapping
  public ResponseEntity<List<ConfigurationDTO>> getAllConfigurations() {
    logger.debug("Getting all configurations");
    List<Configuration> configs = configurationService.getAllConfigurations();
    List<ConfigurationDTO> response =
        configs.stream().map(this::mapToDTO).collect(Collectors.toList());
    return ResponseEntity.ok(response);
  }

  @PutMapping
  public ResponseEntity<ConfigurationDTO> updateConfiguration(
      @Valid @RequestBody ConfigurationDTO dto) {
    logger.info("Updating configuration with key: {}", dto.getConfigKey());
    Configuration config = new Configuration();
    config.setConfigValue(dto.getConfigValue());
    config.setDescription(dto.getDescription());
    Configuration updated = configurationService.updateConfiguration(dto.getConfigKey(), config);
    return ResponseEntity.ok(mapToDTO(updated));
  }

  private ConfigurationDTO mapToDTO(Configuration config) {
    return ConfigurationDTO.builder()
        .id(config.getId())
        .configKey(config.getConfigKey())
        .configValue(config.getConfigValue())
        .description(config.getDescription())
        .build();
  }
}
