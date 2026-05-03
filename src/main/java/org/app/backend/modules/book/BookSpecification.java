package org.app.backend.modules.book;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import org.app.backend.modules.book.dto.BookFilterDTO;
import org.app.backend.modules.book.enums.BookStatus;
import org.springframework.data.jpa.domain.Specification;

public class BookSpecification {
  public static Specification<Book> filter(BookFilterDTO filter) {
    return (root, query, cb) -> {
      List<Predicate> predicates = new ArrayList<>();

      predicates.add(cb.notEqual(root.get("status"), BookStatus.DELETED));

      if (filter != null) {
        if (filter.getQuery() != null && !filter.getQuery().isBlank()) {
          String keyword = "%" + filter.getQuery().toLowerCase() + "%";
          predicates.add(
              cb.or(
                  cb.like(cb.lower(root.get("title")), keyword),
                  cb.like(cb.lower(root.get("author")), keyword),
                  cb.like(cb.lower(root.get("isbn")), keyword)));
        }
        if (filter.getCategoryId() != null) {
          predicates.add(cb.equal(root.get("category").get("id"), filter.getCategoryId()));
        }
        if (filter.getStatus() != null) {
          predicates.add(cb.equal(root.get("status"), filter.getStatus()));
        }
      }
      return cb.and(predicates.toArray(new Predicate[0]));
    };
  }
}
