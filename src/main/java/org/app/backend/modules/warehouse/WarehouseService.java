package org.app.backend.modules.warehouse;

import java.util.List;
import java.util.UUID;
import org.app.backend.modules.warehouse.dto.AddShelfDTO;
import org.app.backend.modules.warehouse.entity.Aisle;
import org.app.backend.modules.warehouse.entity.Floor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface WarehouseService {
  // Floor operations
  Floor createFloor(Floor floor);

  Floor getFloorById(UUID id);

  Floor updateFloor(UUID id, Floor floor);

  void deleteFloor(UUID id);

  List<Floor> getAllFloors();

  // Aisle operations
  Aisle createAisle(Aisle aisle);

  Aisle getAisleById(UUID id);

  Aisle updateAisle(UUID id, Aisle aisle);

  void deleteAisle(UUID id);

  List<Aisle> getAllAisles();

  // Shelf operations (existing)
  Page<Floor> getWarehouseTree(Pageable pageable);

  org.app.backend.modules.warehouse.entity.Shelf createShelf(AddShelfDTO dto);

  void deleteShelf(UUID id);
}
