package org.app.backend.modules.notification;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import org.app.backend.modules.notification.dto.NotificationFilterDTO;
import org.app.backend.modules.notification.enums.NotificationReadStatus;
import org.app.backend.modules.notification.enums.NotificationType;
import org.springframework.data.jpa.domain.Specification;

public class NotificationSpecification {
  public static Specification<Notification> filter(NotificationFilterDTO filter) {
    return (root, query, cb) -> {
      List<Predicate> predicates = new ArrayList<>();

      // Luôn lọc theo userId
      if (filter.getUserId() != null) {
        predicates.add(cb.equal(root.get("user").get("id"), filter.getUserId()));
      }

      if (filter.getType() != null) {
        predicates.add(cb.equal(root.get("type"), filter.getType()));
      }

      if (filter.getReadStatus() != null) {
        predicates.add(cb.equal(root.get("readStatus"), filter.getReadStatus()));
      }

      if (filter.getStartDate() != null) {
        predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), filter.getStartDate()));
      }

      if (filter.getEndDate() != null) {
        predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), filter.getEndDate()));
      }

      return cb.and(predicates.toArray(new Predicate[0]));
    };
  }
}
