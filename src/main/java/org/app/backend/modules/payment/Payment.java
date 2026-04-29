package org.app.backend.modules.payment;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(name = "tbl_payment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Payment {
  @Id
  @GeneratedValue
  @UuidGenerator(style = UuidGenerator.Style.TIME)
  UUID id;

  @Column(nullable = false)
  UUID userId;

  @Column(nullable = false)
  Double amount;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  PaymentStatus paymentStatus;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  PaymentType paymentType;

  UUID fineId;

  UUID userSubscriptionId;

  @CreationTimestamp
  @Column(name = "created_at", updatable = false, nullable = false)
  Instant createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at", nullable = false)
  Instant updatedAt;
}
