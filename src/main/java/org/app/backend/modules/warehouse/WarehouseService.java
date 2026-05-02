package org.app.backend.modules.warehouse;

import java.util.UUID;
import org.app.backend.modules.warehouse.dto.AddShelfDTO;
import org.app.backend.modules.warehouse.entity.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface WarehouseService {
  Page<Floor> getWarehouseTree(Pageable pageable);

  Shelf createShelf(AddShelfDTO dto);

  void deleteShelf(UUID id);
}
