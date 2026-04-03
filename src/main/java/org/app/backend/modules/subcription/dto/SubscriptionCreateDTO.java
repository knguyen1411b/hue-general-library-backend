package org.app.backend.modules.subcription.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SubscriptionCreateDTO {

  String key;
  String name;
  Integer maxBooks;
  Integer price;
  Integer durationDays;
  Integer overdueFeePerDay;
  Integer maxRenewals;
  Integer compensationRate;
}
