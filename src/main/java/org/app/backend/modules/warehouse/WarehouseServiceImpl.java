package org.app.backend.modules.warehouse;

import java.util.List;
import java.util.UUID;
import org.app.backend.modules.warehouse.dto.AddShelfDTO;
import org.app.backend.modules.warehouse.entity.Aisle;
import org.app.backend.modules.warehouse.entity.Floor;
import org.app.backend.modules.warehouse.entity.Shelf;
import org.app.backend.modules.warehouse.exception.AisleNotFoundException;
import org.app.backend.modules.warehouse.exception.FloorNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class WarehouseServiceImpl implements WarehouseService {
  private final org.app.backend.modules.warehouse.repository.FloorRepository floorRepository;
  private final org.app.backend.modules.warehouse.repository.AisleRepository aisleRepository;
  private final org.app.backend.modules.warehouse.repository.ShelfRepository shelfRepository;

  public WarehouseServiceImpl(
      org.app.backend.modules.warehouse.repository.FloorRepository floorRepository,
      org.app.backend.modules.warehouse.repository.AisleRepository aisleRepository,
      org.app.backend.modules.warehouse.repository.ShelfRepository shelfRepository) {
    this.floorRepository = floorRepository;
    this.aisleRepository = aisleRepository;
    this.shelfRepository = shelfRepository;
  }

  // Floor methods
  @Override
  public Floor createFloor(Floor floor) {
    return floorRepository.save(floor);
  }

  @Override
  public Floor getFloorById(UUID id) {
    return floorRepository
        .findById(id)
        .orElseThrow(() -> new FloorNotFoundException(WarehouseMessage.FLOOR_NOT_FOUND));
  }

  @Override
  public Floor updateFloor(UUID id, Floor floor) {
    Floor existing = getFloorById(id);
    existing.setName(floor.getName());
    return floorRepository.save(existing);
  }

  @Override
  public void deleteFloor(UUID id) {
    Floor existing = getFloorById(id);
    floorRepository.delete(existing);
  }

  @Override
  public List<Floor> getAllFloors() {
    return floorRepository.findAll();
  }

  // Aisle methods
  @Override
  public Aisle createAisle(Aisle aisle) {
    return aisleRepository.save(aisle);
  }

  @Override
  public Aisle getAisleById(UUID id) {
    return aisleRepository
        .findById(id)
        .orElseThrow(() -> new AisleNotFoundException(WarehouseMessage.AISLE_NOT_FOUND));
  }

  @Override
  public Aisle updateAisle(UUID id, Aisle aisle) {
    Aisle existing = getAisleById(id);
    existing.setName(aisle.getName());
    return aisleRepository.save(existing);
  }

  @Override
  public void deleteAisle(UUID id) {
    Aisle existing = getAisleById(id);
    aisleRepository.delete(existing);
  }

  @Override
  public List<Aisle> getAllAisles() {
    return aisleRepository.findAll();
  }

  // Shelf methods (existing)
  @Override
  public Page<Floor> getWarehouseTree(Pageable pageable) {
    return floorRepository.findAll(pageable);
  }

  @Override
  public Shelf createShelf(AddShelfDTO dto) {
    Aisle aisle = getAisleById(dto.getAisleId());
    Shelf shelf = new Shelf();
    shelf.setAisle(aisle);
    shelf.setMaxCol(dto.getMaxCol());
    shelf.setMaxRow(dto.getMaxRow());
    return shelfRepository.save(shelf);
  }

  @Override
  public void deleteShelf(UUID id) {
    shelfRepository.deleteById(id);
  }
}
