package org.app.backend.modules.warehouse.entity;

import jakarta.persistence.*;
import java.util.List;
import java.util.UUID;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(name = "tbl_shelf")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Shelf {
  @Id
  @GeneratedValue
  @UuidGenerator(style = UuidGenerator.Style.TIME)
  UUID id;

  String name;
  int maxRow;
  int maxCol;

  @ManyToOne
  @JoinColumn(name = "aisle_id")
  Aisle aisle;

  @OneToMany(mappedBy = "shelf", cascade = CascadeType.ALL)
  List<Position> positions;
}
