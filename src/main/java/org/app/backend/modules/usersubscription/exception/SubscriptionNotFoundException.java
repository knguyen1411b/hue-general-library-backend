package org.app.backend.modules.usersubscription.exception;

public class SubscriptionNotFoundException extends RuntimeException {

  public SubscriptionNotFoundException(String message) {
    super(message);
  }
}
