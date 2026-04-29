package org.app.backend.modules.reservation.exception;

public class ReservationNotFoundException extends RuntimeException {
  public ReservationNotFoundException(String message) {
    super(message);
  }
}
