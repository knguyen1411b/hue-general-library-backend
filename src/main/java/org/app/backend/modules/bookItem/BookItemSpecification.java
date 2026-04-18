package org.app.backend.modules.bookItem;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import lombok.experimental.UtilityClass;
import org.app.backend.modules.bookItem.filter.BookItemFilterDTO;
import org.springframework.data.jpa.domain.Specification;

@UtilityClass
public class BookItemSpecification {
  public Specification<BookItem> filter(BookItemFilterDTO f) {
    return (root, query, cb) -> {
      List<Predicate> predicates = new ArrayList<>();
      // Luôn ẩn các item đã xóa
      predicates.add(cb.notEqual(root.get("status"), BookItemStatus.DELETED));

      if (f.getBarcode() != null && !f.getBarcode().isBlank()) {
        predicates.add(cb.like(root.get("barcode"), "%" + f.getBarcode() + "%"));
      }
      if (f.getBookId() != null) {
        predicates.add(cb.equal(root.get("book").get("id"), f.getBookId()));
      }
      if (f.getStatus() != null) {
        predicates.add(cb.equal(root.get("status"), f.getStatus()));
      }
      if (f.getShelfPositionId() != null) {
        predicates.add(cb.equal(root.get("position").get("id"), f.getShelfPositionId()));
      }

      return cb.and(predicates.toArray(new Predicate[0]));
    };
  }
}
