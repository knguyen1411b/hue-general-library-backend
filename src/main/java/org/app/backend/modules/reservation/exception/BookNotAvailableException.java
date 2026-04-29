package org.app.backend.modules.reservation.exception;

public class BookNotAvailableException extends RuntimeException {
  public BookNotAvailableException(String message) {
    super(message);
  }
}
