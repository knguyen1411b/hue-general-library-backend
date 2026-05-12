package org.app.backend.modules.usersubscription.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSubscriptionCreateDTO {

  @NotNull(message = "User ID is required")
  private UUID userId;

  @NotNull(message = "Subscription ID is required")
  private UUID subscriptionId;
}