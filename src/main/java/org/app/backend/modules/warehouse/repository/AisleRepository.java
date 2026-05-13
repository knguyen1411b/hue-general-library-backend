package org.app.backend.modules.warehouse.repository;

import java.util.List;
import java.util.UUID;
import org.app.backend.modules.warehouse.entity.Aisle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AisleRepository extends JpaRepository<Aisle, UUID> {
  List<Aisle> findByFloorId(UUID floorId);

  List<Aisle> findByFloorIdIn(List<UUID> floorIds);

  boolean existsByFloorIdAndNameIgnoreCase(UUID floorId, String name);
}
