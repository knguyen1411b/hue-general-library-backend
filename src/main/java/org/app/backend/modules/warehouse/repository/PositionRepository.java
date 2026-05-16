package org.app.backend.modules.warehouse.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.app.backend.modules.warehouse.entity.Position;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PositionRepository extends JpaRepository<Position, UUID> {
  List<Position> findByShelfId(UUID shelfId);

  @EntityGraph(attributePaths = {"shelf", "shelf.aisle", "shelf.aisle.floor"})
  Optional<Position> findFirstByShelfId(UUID shelfId);

  @EntityGraph(attributePaths = {"shelf", "shelf.aisle", "shelf.aisle.floor"})
  Optional<Position> findWithHierarchyById(UUID id);
}
