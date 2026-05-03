package org.app.backend.modules.category.filter;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import org.app.backend.modules.category.Category;
import org.app.backend.modules.category.dto.CategoryFilterDTO;
import org.app.backend.modules.category.enums.CategoryStatus;
import org.springframework.data.jpa.domain.Specification;

public class CategorySpecification {
  public static Specification<Category> filter(CategoryFilterDTO filter) {
    return (root, query, criteriaBuilder) -> {
      List<Predicate> predicates = new ArrayList<>();

      predicates.add(criteriaBuilder.equal(root.get("status"), CategoryStatus.ACTIVE));

      if (filter.getTitle() != null && !filter.getTitle().isEmpty()) {
        predicates.add(
            criteriaBuilder.like(
                criteriaBuilder.lower(root.get("title")),
                "%" + filter.getTitle().toLowerCase() + "%"));
      }

      return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    };
  }
}
