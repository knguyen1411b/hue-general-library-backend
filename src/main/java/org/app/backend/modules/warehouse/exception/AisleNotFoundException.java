package org.app.backend.modules.warehouse.exception;

public class AisleNotFoundException extends RuntimeException {
  public AisleNotFoundException(String message) {
    super(message);
  }
}
