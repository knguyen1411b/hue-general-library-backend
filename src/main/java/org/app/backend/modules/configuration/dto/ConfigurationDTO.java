package org.app.backend.modules.configuration.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfigurationDTO {
  private Long id;

  @NotBlank(message = "Config key is required")
  private String configKey;

  @NotBlank(message = "Config value is required")
  private String configValue;

  private String description;
}
