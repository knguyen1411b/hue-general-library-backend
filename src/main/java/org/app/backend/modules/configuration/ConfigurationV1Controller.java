package org.app.backend.modules.configuration;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.app.backend.common.dto.DataApiResponse;
import org.app.backend.common.swagger.BadRequestApiResponse;
import org.app.backend.common.swagger.NotFoundApiResponse;
import org.app.backend.common.swagger.UnauthorizedApiResponse;
import org.app.backend.modules.configuration.dto.ConfigurationDTO;
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

  @Operation(
      summary = "Lấy danh sách cấu hình hệ thống",
      responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = DataApiResponseConfigurationList.class)))
      })
  @UnauthorizedApiResponse
  @GetMapping
  public DataApiResponse<List<ConfigurationDTO>> getAllConfigurations() {
    List<Configuration> configs = configurationService.getAllConfigurations();
    List<ConfigurationDTO> response =
        configs.stream().map(this::mapToDTO).collect(Collectors.toList());
    return DataApiResponse.success(response, "Lấy danh sách cấu hình hệ thống thành công");
  }

  @Operation(
      summary = "Cập nhật cấu hình hệ thống theo khóa cấu hình",
      responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = DataApiResponseConfigurationDTO.class)))
      })
  @BadRequestApiResponse
  @NotFoundApiResponse
  @UnauthorizedApiResponse
  @PutMapping
  public DataApiResponse<ConfigurationDTO> updateConfiguration(
      @Valid @RequestBody ConfigurationDTO dto) {
    Configuration config = new Configuration();
    config.setConfigValue(dto.getConfigValue());
    config.setDescription(dto.getDescription());
    Configuration updated = configurationService.updateConfiguration(dto.getConfigKey(), config);
    return DataApiResponse.success(mapToDTO(updated), "Cập nhật cấu hình hệ thống thành công");
  }

  private ConfigurationDTO mapToDTO(Configuration config) {
    return ConfigurationDTO.builder()
        .id(config.getId())
        .configKey(config.getConfigKey())
        .configValue(config.getConfigValue())
        .description(config.getDescription())
        .build();
  }

  public static class DataApiResponseConfigurationDTO extends DataApiResponse<ConfigurationDTO> {}

  public static class DataApiResponseConfigurationList
      extends DataApiResponse<List<ConfigurationDTO>> {}
}
