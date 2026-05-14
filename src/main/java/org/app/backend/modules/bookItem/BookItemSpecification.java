package org.app.backend.modules.bookItem;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import lombok.experimental.UtilityClass;
import org.app.backend.modules.bookItem.enums.BookItemStatus;
import org.app.backend.modules.bookItem.filter.BookItemFilterDTO;
import org.app.backend.modules.warehouse.entity.Aisle;
import org.app.backend.modules.warehouse.entity.Floor;
import org.app.backend.modules.warehouse.entity.Position;
import org.app.backend.modules.warehouse.entity.Shelf;
import org.springframework.data.jpa.domain.Specification;

@UtilityClass
public class BookItemSpecification {
  public Specification<BookItem> filter(BookItemFilterDTO f) {
    return (root, query, cb) -> {
      List<Predicate> predicates = new ArrayList<>();

      predicates.add(cb.notEqual(root.get("status"), BookItemStatus.DELETED));

      if (f != null) {
        if (f.getBarcode() != null && !f.getBarcode().isBlank()) {
          predicates.add(
              cb.like(
                  cb.lower(root.get("barcode")),
                  "%" + f.getBarcode().toLowerCase(Locale.ROOT) + "%"));
        }
        if (f.getBookId() != null) {
          predicates.add(cb.equal(root.get("book").get("id"), f.getBookId()));
        }
        if (f.getStatus() != null) {
          predicates.add(cb.equal(root.get("status"), f.getStatus()));
        }
        if (f.getShelfPositionId() != null) {
          predicates.add(cb.equal(shelfJoin(root).get("id"), f.getShelfPositionId()));
        }
        if (f.getShelfName() != null && !f.getShelfName().isBlank()) {
          predicates.add(
              cb.like(
                  cb.lower(shelfJoin(root).get("name")),
                  "%" + f.getShelfName().toLowerCase(Locale.ROOT) + "%"));
        }
        if (f.getAisleName() != null && !f.getAisleName().isBlank()) {
          predicates.add(
              cb.like(
                  cb.lower(aisleJoin(root).get("name")),
                  "%" + f.getAisleName().toLowerCase(Locale.ROOT) + "%"));
        }
        if (f.getFloorName() != null && !f.getFloorName().isBlank()) {
          predicates.add(
              cb.like(
                  cb.lower(floorJoin(root).get("name")),
                  "%" + f.getFloorName().toLowerCase(Locale.ROOT) + "%"));
        }
      }

      return cb.and(predicates.toArray(new Predicate[0]));
    };
  }

  private Join<BookItem, Position> positionJoin(jakarta.persistence.criteria.Root<BookItem> root) {
    return root.join("position", JoinType.LEFT);
  }

  private Join<Position, Shelf> shelfJoin(jakarta.persistence.criteria.Root<BookItem> root) {
    return positionJoin(root).join("shelf", JoinType.LEFT);
  }

  private Join<Shelf, Aisle> aisleJoin(jakarta.persistence.criteria.Root<BookItem> root) {
    return shelfJoin(root).join("aisle", JoinType.LEFT);
  }

  private Join<Aisle, Floor> floorJoin(jakarta.persistence.criteria.Root<BookItem> root) {
    return aisleJoin(root).join("floor", JoinType.LEFT);
  }
}
