package org.app.backend.modules.warehouse.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter @NoArgsConstructor
public class Position {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int rowIndex;
    private int colIndex;
    private int bookCount = 0;

    @ManyToOne
    @JoinColumn(name = "shelf_id")
    private Shelf shelf;
}
