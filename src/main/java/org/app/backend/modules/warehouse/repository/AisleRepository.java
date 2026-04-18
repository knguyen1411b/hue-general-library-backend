package org.app.backend.modules.warehouse.repository;

import java.util.UUID;
import org.app.backend.modules.warehouse.entity.Aisle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AisleRepository extends JpaRepository<Aisle, UUID> {}
