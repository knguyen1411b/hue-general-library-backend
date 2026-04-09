package org.app.backend.modules.usersubscription.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
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
public class UserSubscriptionResponseDTO {

  private UUID id;

  // User information
  private UUID userId;
  private String username;
  private String fullName;
  private String email;

  // Subscription information
  private UUID subscriptionId;
  private String subscriptionKey;
  private String subscriptionName;
  private Integer subscriptionDurationDays;
  private Integer subscriptionOverdueFeePerDay;
  private Integer subscriptionMaxRenewals;
  private Integer subscriptionCompensationRate;

  @JsonFormat(pattern = "yyyy-MM-dd")
  private LocalDate startDate;

  @JsonFormat(pattern = "yyyy-MM-dd")
  private LocalDate endDate;

  private UserSubscriptionStatus status;

  private Integer maxBooks;
  private Integer price;
}
