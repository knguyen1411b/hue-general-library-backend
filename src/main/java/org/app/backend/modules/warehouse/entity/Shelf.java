package org.app.backend.modules.warehouse.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Getter @Setter @NoArgsConstructor
public class Shelf {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private int maxRow;
    private int maxCol;

    @ManyToOne
    @JoinColumn(name = "aisle_id")
    private Aisle aisle;

    @OneToMany(mappedBy = "shelf", cascade = CascadeType.ALL)
    private List<Position> positions;
}
