package org.app.backend.modules.subcription.dto;

import java.util.UUID;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.app.backend.modules.subcription.SubscriptionStatus;

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
