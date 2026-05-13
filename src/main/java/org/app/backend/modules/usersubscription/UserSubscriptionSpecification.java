package org.app.backend.modules.usersubscription;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.app.backend.modules.usersubscription.dto.UserSubscriptionFilterDTO;
import org.springframework.data.jpa.domain.Specification;

public class UserSubscriptionSpecification {
  public static Specification<UserSubscription> filter(UserSubscriptionFilterDTO filter) {
    return (root, query, cb) -> {
      List<Predicate> predicates = new ArrayList<>();

      if (filter != null) {
        if (filter.getUserId() != null) {
          predicates.add(cb.equal(root.get("user").get("id"), filter.getUserId()));
        }
        if (filter.getSubscriptionId() != null) {
          predicates.add(cb.equal(root.get("subscription").get("id"), filter.getSubscriptionId()));
        }
        if (filter.getStatus() != null) {
          predicates.add(cb.equal(root.get("status"), filter.getStatus()));
        }
        if (filter.getExcludeStatus() != null) {
          predicates.add(cb.notEqual(root.get("status"), filter.getExcludeStatus()));
        }
      }
      return cb.and(predicates.toArray(new Predicate[0]));
    };
  }
}