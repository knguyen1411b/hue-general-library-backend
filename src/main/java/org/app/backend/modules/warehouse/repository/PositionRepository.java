package org.app.backend.modules.warehouse.repository;

import java.util.List;
import java.util.UUID;
import org.app.backend.modules.warehouse.entity.Position;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PositionRepository extends JpaRepository<Position, UUID> {
  List<Position> findByShelfIdOrderByRowIndexAscColIndexAsc(UUID shelfId);
}
