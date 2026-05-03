package org.app.backend.modules.configuration;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.app.backend.modules.configuration.dto.ConfigurationDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/configurations")
@RequiredArgsConstructor
@Tag(name = "Cấu hình hệ thống (V1)")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Validated
public class ConfigurationV1Controller {
  ConfigurationService configurationService;

  @GetMapping
  public ResponseEntity<List<ConfigurationDTO>> getAllConfigurations() {
    List<Configuration> configs = configurationService.getAllConfigurations();
    List<ConfigurationDTO> response =
        configs.stream().map(this::mapToDTO).collect(Collectors.toList());
    return ResponseEntity.ok(response);
  }

  @PutMapping
  public ResponseEntity<ConfigurationDTO> updateConfiguration(
      @Valid @RequestBody ConfigurationDTO dto) {
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
