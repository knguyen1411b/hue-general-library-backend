package org.app.backend.modules.usersubscription.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.app.backend.modules.usersubscription.UserSubscriptionStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSubscriptionDTO {

  private UUID id;

  @NotNull(message = "User ID is required")
  private UUID userId;

  @NotNull(message = "Subscription ID is required")
  private UUID subscriptionId;

  @NotNull(message = "Start date is required")
  private LocalDate startDate;

  @NotNull(message = "End date is required")
  @Future(message = "End date must be in the future")
  private LocalDate endDate;

  private UserSubscriptionStatus status;

  @NotNull(message = "Max books is required")
  @Min(value = 1, message = "Max books must be at least 1")
  @Max(value = 1000, message = "Max books must not exceed 1000")
  private Integer maxBooks;

  @NotNull(message = "Price is required")
  @Min(value = 0, message = "Price must be greater than or equal to 0")
  @Max(value = 100000000, message = "Price must not exceed 100,000,000")
  private Integer price;
}
