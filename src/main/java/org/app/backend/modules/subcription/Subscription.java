package org.app.backend.modules.subcription;

import jakarta.persistence.*;
import java.util.UUID;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(name = "tbl_subscription")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Subscription {

    @Id
    @GeneratedValue
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    UUID id;

    @Column(nullable = false, unique = true, length = 50)
    String key;

    @Column(nullable = false, length = 255)
    String name;

    @Column(name = "max_books", nullable = false)
    Integer maxBooks;

    @Column(nullable = false)
    Integer price;

    @Column(name = "duration_days", nullable = false)
    Integer durationDays;

    @Column(name = "overdue_fee_per_day", nullable = false)
    Integer overdueFeePerDay;

    @Column(name = "max_renewals", nullable = false)
    Integer maxRenewals;

    @Column(name = "compensation_rate", nullable = false)
    Integer compensationRate;
}
