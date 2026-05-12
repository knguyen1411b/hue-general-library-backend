package org.app.backend.modules.configuration;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.app.backend.common.exception.AppException;
import org.app.backend.modules.auth.security.CustomUserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

@ExtendWith(MockitoExtension.class)
class ConfigurationServiceImplTest {

  @Mock private ConfigurationRepository configurationRepository;
  @Mock private ModelMapper modelMapper;

  @InjectMocks private ConfigurationServiceImpl configurationService;

  private Configuration mockConfig;
  private CustomUserDetails mockUserDetails;
  private String configKey = "MAX_BOOKS";

  @BeforeEach
  void setUp() {
    mockConfig = new Configuration();
    mockConfig.setConfigKey(configKey);
    mockConfig.setConfigValue("5");

    mockUserDetails = new CustomUserDetails();
    mockUserDetails.setId(UUID.randomUUID());
    mockUserDetails.setUsername("admin");
  }

  @Test
  @DisplayName("Get All Configurations - Success")
  void testGetConfigurations_Success() {
    when(configurationRepository.findAll()).thenReturn(List.of(mockConfig));

    var result = configurationService.getAllConfigurations();

    assertNotNull(result);
    assertEquals("5", result.get(0).getConfigValue());
  }

  @Test
  @DisplayName("Update Configuration - Success")
  void testUpdateConfiguration_Success() {
    Configuration dto = new Configuration();
    dto.setConfigKey(configKey);
    dto.setConfigValue("10");

    when(configurationRepository.findByConfigKey(configKey)).thenReturn(Optional.of(mockConfig));
    when(configurationRepository.save(mockConfig)).thenReturn(mockConfig);

    configurationService.updateConfiguration(configKey, dto);

    assertEquals("10", mockConfig.getConfigValue());
    verify(configurationRepository, times(1)).save(mockConfig);
  }

  @Test
  @DisplayName("Update Configuration - Not Found")
  void testUpdateConfiguration_NotFound() {
    Configuration dto = new Configuration();
    dto.setConfigKey("UNKNOWN_KEY");
    dto.setConfigValue("10");

    when(configurationRepository.findByConfigKey("UNKNOWN_KEY")).thenReturn(Optional.empty());

    AppException exception =
        assertThrows(
            AppException.class, () -> configurationService.updateConfiguration("UNKNOWN_KEY", dto));
    assertEquals(ConfigurationMessage.CONFIG_NOT_FOUND.getMessage(), exception.getMessage());
  }
}
