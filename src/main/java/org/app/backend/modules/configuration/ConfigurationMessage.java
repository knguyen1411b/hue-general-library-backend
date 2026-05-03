package org.app.backend.modules.configuration;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum ConfigurationMessage {
  CONFIG_NOT_FOUND("Không tìm thấy cấu hình");

  String message;
}
