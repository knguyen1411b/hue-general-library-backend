package org.app.backend.modules.usersubscription.exception;

public class UserSubscriptionAlreadyExistsException extends RuntimeException {

  public UserSubscriptionAlreadyExistsException(String message) {
    super(message);
  }
}
