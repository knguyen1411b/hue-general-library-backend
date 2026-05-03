package org.app.backend.modules.warehouse.repository;

import java.util.UUID;
import org.app.backend.modules.warehouse.entity.Floor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FloorRepository extends JpaRepository<Floor, UUID> {
  boolean existsByNameIgnoreCase(String name);
}
