package org.app.backend.modules.warehouse;

import java.util.List;
import java.util.UUID;
import org.app.backend.modules.auth.security.CustomUserDetails;
import org.app.backend.modules.warehouse.dto.AddShelfDTO;
import org.app.backend.modules.warehouse.dto.AisleCreateDTO;
import org.app.backend.modules.warehouse.dto.AisleDTO;
import org.app.backend.modules.warehouse.dto.AisleUpdateDTO;
import org.app.backend.modules.warehouse.dto.FloorCreateDTO;
import org.app.backend.modules.warehouse.dto.FloorDTO;
import org.app.backend.modules.warehouse.dto.FloorUpdateDTO;
import org.app.backend.modules.warehouse.dto.ShelfDTO;
import org.app.backend.modules.warehouse.dto.SimpleFloorDTO;

public interface WarehouseService {

  FloorDTO createFloor(FloorCreateDTO dto, CustomUserDetails actor);

  FloorDTO updateFloor(UUID id, FloorUpdateDTO dto, CustomUserDetails actor);

  void deleteFloor(UUID id, CustomUserDetails actor);

  FloorDTO getFloorById(UUID id);

  List<SimpleFloorDTO> getAllFloors();

  List<FloorDTO> getWarehouseTree();

  AisleDTO createAisle(AisleCreateDTO dto, CustomUserDetails actor);

  AisleDTO updateAisle(UUID id, AisleUpdateDTO dto, CustomUserDetails actor);

  void deleteAisle(UUID id, CustomUserDetails actor);

  AisleDTO getAisleById(UUID id);

  List<AisleDTO> getAllAisles();

  ShelfDTO createShelf(AddShelfDTO dto, CustomUserDetails actor);

  void deleteShelf(UUID id, CustomUserDetails actor);

  List<ShelfDTO> getAllShelves();
}
