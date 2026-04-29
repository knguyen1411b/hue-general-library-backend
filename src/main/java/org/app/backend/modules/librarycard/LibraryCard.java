package org.app.backend.modules.librarycard;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.Instant;
import java.util.UUID;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "tbl_library_card")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LibraryCard {
  @Id
  @GeneratedValue
  @UuidGenerator(style = UuidGenerator.Style.TIME)
  UUID id;

  @Column(nullable = false)
  UUID userId;

  @Column(nullable = false)
  LocalDate issueDate;

  @Column(nullable = false)
  LocalDate expiryDate;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  CardStatus status;

  @CreationTimestamp
  @Column(name = "created_at", updatable = false, nullable = false)
  Instant createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at", nullable = false)
  Instant updatedAt;
}
