package org.app.backend.modules.warehouse.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.app.backend.modules.warehouse.entity.Shelf;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShelfRepository extends JpaRepository<Shelf, UUID> {
  List<Shelf> findByAisleId(UUID aisleId);

  List<Shelf> findByAisleIdIn(List<UUID> aisleIds);

  @EntityGraph(attributePaths = {"aisle", "aisle.floor"})
  Optional<Shelf> findWithHierarchyById(UUID id);
}
