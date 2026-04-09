package org.app.backend.modules.subscription.dto;

import java.util.UUID;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.app.backend.modules.subscription.SubscriptionStatus;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SubscriptionDTO {

  UUID id;
  String key;
  String name;
  Integer maxBooks;
  Integer price;
  Integer durationDays;
  Integer overdueFeePerDay;
  Integer maxRenewals;
  Integer compensationRate;
  SubscriptionStatus status;
}
