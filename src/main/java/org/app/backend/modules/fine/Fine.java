package org.app.backend.modules.fine;

import jakarta.persistence.*;
import java.util.UUID;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.app.backend.modules.fine.enums.FineStatus;
import org.app.backend.modules.rental.Rental;
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

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "rental_id", nullable = false)
  Rental rental;

  @Column(nullable = false)
  Integer amount;

  @Column(nullable = false, length = 500)
  String reason;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  @Builder.Default
  FineStatus status = FineStatus.UNPAID;
}
