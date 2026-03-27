package org.app.backend.modules.audit;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import lombok.experimental.UtilityClass;
import org.app.backend.modules.audit.dto.AuditLogFilterDTO;
import org.springframework.data.jpa.domain.Specification;

@UtilityClass
public class AuditLogSpecification {
  public Specification<AuditLog> filter(AuditLogFilterDTO filter) {
    return (root, query, cb) -> {
      if (filter == null) {
        return cb.conjunction();
      }

      List<Predicate> predicates = new ArrayList<>();

      if (filter.getUserId() != null) {
        predicates.add(cb.equal(root.get("userId"), filter.getUserId()));
      }

      if (filter.getAction() != null) {
        predicates.add(cb.equal(root.get("action"), filter.getAction()));
      }

      if (filter.getEntityName() != null) {
        predicates.add(cb.equal(root.get("entityName"), filter.getEntityName()));
      }

      if (filter.getStatus() != null) {
        predicates.add(cb.equal(root.get("status"), filter.getStatus()));
      }

      if (filter.getQ() != null && !filter.getQ().isBlank()) {
        String like = "%" + filter.getQ().toLowerCase() + "%";
        predicates.add(
            cb.or(
                cb.like(cb.lower(root.get("username")), like),
                cb.like(cb.lower(root.get("action")), like),
                cb.like(cb.lower(root.get("entityName")), like),
                cb.like(cb.lower(root.get("entityId")), like),
                cb.like(cb.lower(root.get("message")), like)));
      }

      return cb.and(predicates.toArray(new Predicate[0]));
    };
  }
}
