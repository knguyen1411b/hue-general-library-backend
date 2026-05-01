package org.app.backend.modules.rental;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.app.backend.modules.bookItem.BookItem;
import org.app.backend.modules.rental.enums.RentalStatus;
import org.app.backend.modules.user.User;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

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

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "book_item_id", nullable = false)
  BookItem bookItem;

  @CreationTimestamp
  @Column(name = "rent_date", updatable = false, nullable = false)
  Instant rentDate;

  @Column(name = "due_date", nullable = false)
  Instant dueDate;

  @Column(name = "return_date")
  Instant returnDate;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  RentalStatus status;
}
