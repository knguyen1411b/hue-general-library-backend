package org.app.backend.modules.usersubscription.dto;

import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.app.backend.modules.usersubscription.UserSubscriptionStatus;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserSubscriptionFilterDTO {

  UUID userId;
  UUID subscriptionId;
  UserSubscriptionStatus status;

  /**
   * Loại trừ các bản ghi có status này khỏi kết quả.
   * Ví dụ: khi USER gọi, sẽ tự động exclude CANCELED.
   */
  UserSubscriptionStatus excludeStatus;
}