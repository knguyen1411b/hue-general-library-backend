package org.app.backend.modules.book;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.app.backend.modules.category.Category;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "tbl_book")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Book { // Có thể extends BaseEntity để có createdAt, updatedAt
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  UUID id;

  @Column(nullable = false)
  String title;

  @Column(nullable = false)
  String author;

  @Column(unique = true, nullable = false)
  String isbn;

  @Column(columnDefinition = "TEXT")
  String description;

  Integer publishedYear;

  String thumbnailUrl;

  @Column(nullable = false)
  Integer price; // Giá tiền để tính đền bù 120%

  @Column(nullable = false)
  String publishers;

  @Column(nullable = false)
  Integer count;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "category_id")
  Category category;

  @CreationTimestamp
  @Column(name = "created_at", updatable = false, nullable = false)
  Instant createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at", nullable = false)
  Instant updatedAt;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  @Builder.Default
  BookStatus status = BookStatus.ACTIVE;
}
