package org.app.backend.modules.warehouse.entity;

import jakarta.persistence.*;
import java.util.List;
import java.util.UUID;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.app.backend.modules.warehouse.AisleStatus;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(name = "tbl_aisle")
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Aisle {
  @Id
  @GeneratedValue
  @UuidGenerator(style = UuidGenerator.Style.TIME)
  UUID id;

  String name;

  @Enumerated(EnumType.STRING)
  AisleStatus status;

  @ManyToOne
  @JoinColumn(name = "floor_id")
  Floor floor;

  @OneToMany(mappedBy = "aisle", cascade = CascadeType.ALL)
  List<Shelf> shelves;
}
