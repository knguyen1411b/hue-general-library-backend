package org.app.backend.modules.usersubscription;

public final class UserSubscriptionMessage {

  private UserSubscriptionMessage() {}

  // Success messages
  public static final String CREATED_SUCCESS = "user.subscription.created.success";
  public static final String UPDATED_SUCCESS = "user.subscription.updated.success";
  public static final String DELETED_SUCCESS = "user.subscription.deleted.success";
  public static final String FOUND_SUCCESS = "user.subscription.found.success";
  public static final String LIST_SUCCESS = "user.subscription.list.success";

  // Error messages
  public static final String NOT_FOUND = "user.subscription.not.found";
  public static final String INVALID_STATUS = "user.subscription.invalid.status";
  public static final String USER_NOT_FOUND = "user.subscription.user.not.found";
  public static final String SUBSCRIPTION_NOT_FOUND = "user.subscription.subscription.not.found";
  public static final String ALREADY_EXISTS = "user.subscription.already.exists";
  public static final String END_DATE_BEFORE_START_DATE =
      "user.subscription.end.date.before.start.date";
  public static final String MAX_BOOKS_EXCEEDED = "user.subscription.max.books.exceeded";
  public static final String SUBSCRIPTION_EXPIRED = "user.subscription.expired";
  public static final String SUBSCRIPTION_CANCELED = "user.subscription.canceled";

  // Validation messages
  public static final String VALIDATION_START_DATE_REQUIRED =
      "user.subscription.validation.start.date.required";
  public static final String VALIDATION_END_DATE_REQUIRED =
      "user.subscription.validation.end.date.required";
  public static final String VALIDATION_USER_REQUIRED =
      "user.subscription.validation.user.required";
  public static final String VALIDATION_SUBSCRIPTION_REQUIRED =
      "user.subscription.validation.subscription.required";
  public static final String VALIDATION_MAX_BOOKS_REQUIRED =
      "user.subscription.validation.max.books.required";
  public static final String VALIDATION_PRICE_REQUIRED =
      "user.subscription.validation.price.required";

  // Log messages
  public static final String LOG_CREATING =
      "Creating user subscription for user: {}, subscription: {}";
  public static final String LOG_UPDATING = "Updating user subscription with id: {}";
  public static final String LOG_DELETING = "Deleting user subscription with id: {}";
  public static final String LOG_FOUND = "Found user subscription with id: {}";
  public static final String LOG_LISTING = "Listing user subscriptions";
  public static final String LOG_CHECKING_STATUS =
      "Checking status for user subscription with id: {}";
}
