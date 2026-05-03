package org.app.backend.modules.usersubscription;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.UUID;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.app.backend.modules.subscription.Subscription;
import org.app.backend.modules.user.User;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(
    name = "tbl_user_subscription",
    indexes = {
      @Index(name = "idx_user_subscription_user_id", columnList = "user_id"),
      @Index(name = "idx_user_subscription_subscription_id", columnList = "subscription_id"),
      @Index(name = "idx_user_subscription_status", columnList = "status"),
      @Index(name = "idx_user_subscription_start_date", columnList = "start_date"),
      @Index(name = "idx_user_subscription_end_date", columnList = "end_date"),
    })
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserSubscription {

  @Id
  @GeneratedValue
  @UuidGenerator(style = UuidGenerator.Style.TIME)
  UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "subscription_id", nullable = false)
  Subscription subscription;

  @Column(name = "start_date", nullable = false)
  LocalDate startDate;

  @Column(name = "end_date", nullable = false)
  LocalDate endDate;

  @Builder.Default
  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  UserSubscriptionStatus status = UserSubscriptionStatus.ACTIVE;

  @Column(name = "max_books", nullable = false)
  Integer maxBooks;

  @Column(nullable = false)
  Integer price;

  public int getOverdueFeePerDay() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'getOverdueFeePerDay'");
  }

  public int getDurationDays() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'getDurationDays'");
  }

  public int getCompensationRate() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'getCompensationRate'");
  }
}
