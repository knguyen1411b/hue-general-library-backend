package org.app.backend.modules.user;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import lombok.experimental.UtilityClass;
import org.app.backend.modules.user.dto.UserFilterDTO;
import org.springframework.data.jpa.domain.Specification;

@UtilityClass
public class UserSpecification {
  public Specification<User> filter(UserFilterDTO filter) {
    return (root, query, cb) -> {
      if (filter == null) {
        return cb.conjunction();
      }
      List<Predicate> predicates = new ArrayList<>();

      if (filter.getQ() != null && !filter.getQ().isBlank()) {
        String like = "%" + filter.getQ().toLowerCase() + "%";
        predicates.add(
            cb.or(
                cb.like(cb.lower(root.get("username")), like),
                cb.like(cb.lower(root.get("email")), like),
                cb.like(cb.lower(root.get("fullName")), like),
                cb.like(cb.lower(root.get("phone")), like),
                cb.like(cb.lower(root.get("address")), like)));
      }
      if (filter.getStatus() != null) {
        predicates.add(cb.equal(root.get("status"), filter.getStatus()));
      }
      if (filter.getRole() != null) {
        predicates.add(cb.equal(root.get("role"), filter.getRole()));
      }
      return cb.and(predicates.toArray(new Predicate[0]));
    };
  }
}
