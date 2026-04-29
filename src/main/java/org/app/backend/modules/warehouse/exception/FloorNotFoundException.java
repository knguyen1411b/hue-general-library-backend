package org.app.backend.modules.warehouse.exception;

public class FloorNotFoundException extends RuntimeException {
  public FloorNotFoundException(String message) {
    super(message);
  }
}
