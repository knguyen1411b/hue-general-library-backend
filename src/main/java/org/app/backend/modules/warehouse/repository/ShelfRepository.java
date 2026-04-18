package org.app.backend.modules.warehouse.repository;

import java.util.UUID;
import org.app.backend.modules.warehouse.entity.Shelf;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShelfRepository extends JpaRepository<Shelf, UUID> {}
