package org.app.backend.modules.rental;

import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;
import org.app.backend.modules.rental.enums.RentalStatus;


@Entity
@Table(name = "tbl_rental")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Rental {
  @Id
  @GeneratedValue
  @UuidGenerator(style = UuidGenerator.Style.TIME)
  UUID id;

  @Column(nullable = false)
  UUID userId;

  @Column(nullable = false)
  UUID bookItemId;

  @Column(nullable = false)
  LocalDate rentDate;

  @Column(nullable = false)
  LocalDate dueDate;

  LocalDate returnDate;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  RentalStatus status;

  @CreationTimestamp
  @Column(name = "created_at", updatable = false, nullable = false)
  Instant createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at", nullable = false)
  Instant updatedAt;
}
