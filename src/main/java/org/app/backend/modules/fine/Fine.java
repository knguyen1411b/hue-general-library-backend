package org.app.backend.modules.fine;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(name = "tbl_fine")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Fine {
  @Id
  @GeneratedValue
  @UuidGenerator(style = UuidGenerator.Style.TIME)
  UUID id;

  @Column(nullable = false)
  UUID rentalId;

  @Column(nullable = false)
  Double amount;

  @Column(nullable = false)
  String reason;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  FineStatus status;

  @CreationTimestamp
  @Column(name = "created_at", updatable = false, nullable = false)
  Instant createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at", nullable = false)
  Instant updatedAt;
}
