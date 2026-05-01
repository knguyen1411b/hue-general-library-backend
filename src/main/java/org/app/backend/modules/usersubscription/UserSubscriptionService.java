package org.app.backend.modules.usersubscription;

import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserSubscriptionService {
  // CRUD operations
  UserSubscription create(UserSubscription userSubscription);

  UserSubscription getById(UUID id);

  UserSubscription update(UUID id, UserSubscription userSubscription);

  void delete(UUID id);

  Page<UserSubscription> getAll(Pageable pageable);

  List<UserSubscription> getAll();
}
