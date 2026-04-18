package org.app.backend.modules.warehouse.entity;

import jakarta.persistence.*;
import java.util.UUID;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(name = "tbl_position")
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Position {
  @Id
  @GeneratedValue
  @UuidGenerator(style = UuidGenerator.Style.TIME)
  UUID id;

  int rowIndex;
  int colIndex;
  int bookCount = 0;

  @ManyToOne
  @JoinColumn(name = "shelf_id")
  Shelf shelf;
}
