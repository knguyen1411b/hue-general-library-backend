package org.app.backend.modules.usersubscription.dto;

import java.time.LocalDate;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.app.backend.modules.usersubscription.UserSubscriptionStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserSubscriptionUpdateDTO {

  LocalDate startDate;

  LocalDate endDate;

  UserSubscriptionStatus status;
}
