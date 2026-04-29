package org.app.backend.modules.configuration.exception;

public class ConfigurationNotFoundException extends RuntimeException {
  public ConfigurationNotFoundException(String message) {
    super(message);
  }
}
