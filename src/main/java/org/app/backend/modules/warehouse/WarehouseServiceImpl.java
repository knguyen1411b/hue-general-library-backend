package org.app.backend.modules.warehouse;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.app.backend.common.exception.AppException;
import org.app.backend.modules.warehouse.dto.AddShelfDTO;
import org.app.backend.modules.warehouse.entity.*;
import org.app.backend.modules.warehouse.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class WarehouseServiceImpl implements WarehouseService {

  FloorRepository floorRepo;
  AisleRepository aisleRepo;
  ShelfRepository shelfRepo;
  PositionRepository positionRepo;

  @Override
  @Transactional(readOnly = true)
  public Page<Floor> getWarehouseTree(Pageable pageable) {
    return floorRepo.findAll(pageable);
  }

  @Override
  @Transactional
  public Shelf createShelf(AddShelfDTO dto) {
    Aisle aisle =
        aisleRepo
            .findById(dto.getAisleId())
            .orElseThrow(
                () ->
                    new AppException(
                        HttpStatus.BAD_REQUEST, WarehouseMessage.ALREADY_EXIST.getMessage()));

    Shelf shelf =
        Shelf.builder()
            .name(dto.getName())
            .maxRow(dto.getMaxRow())
            .maxCol(dto.getMaxCol())
            .aisle(aisle)
            .build();

    Shelf savedShelf = shelfRepo.save(shelf);

    for (int r = 1; r <= dto.getMaxRow(); r++) {
      for (int c = 1; c <= dto.getMaxCol(); c++) {
        Position pos = new Position();
        pos.setRowIndex(r);
        pos.setColIndex(c);
        pos.setShelf(savedShelf);
        pos.setBookCount(0);
        positionRepo.save(pos);
      }
    }
    return savedShelf;
  }

  @Transactional
  public void deleteShelf(UUID id) {
    Shelf shelf = shelfRepo.findById(id).orElseThrow();

    boolean hasBooks = shelf.getPositions().stream().anyMatch(p -> p.getBookCount() > 0);

    if (hasBooks) {
      throw new AppException(HttpStatus.BAD_REQUEST, WarehouseMessage.CANNOT_DELETE.getMessage());
    }

    shelfRepo.delete(shelf);
  }
}
