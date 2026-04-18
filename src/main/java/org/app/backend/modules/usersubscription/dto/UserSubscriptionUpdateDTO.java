package org.app.backend.modules.usersubscription.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.UUID;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.app.backend.modules.usersubscription.UserSubscriptionStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserSubscriptionUpdateDTO {

  @NotNull(message = "User ID is required")
  UUID userId;

  @NotNull(message = "Subscription ID is required")
  UUID subscriptionId;

  @NotNull(message = "Start date is required")
  LocalDate startDate;

  @NotNull(message = "End date is required")
  @Future(message = "End date must be in the future")
  LocalDate endDate;

  UserSubscriptionStatus status;

  @Min(value = 1, message = "Max books must be at least 1")
  @Max(value = 1000, message = "Max books must not exceed 1000")
  Integer maxBooks;

  @Min(value = 0, message = "Price must be greater than or equal to 0")
  @Max(value = 100000000, message = "Price must not exceed 100,000,000")
  Integer price;
}
