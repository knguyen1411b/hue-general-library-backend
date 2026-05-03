package org.app.backend.modules.warehouse;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.app.backend.modules.warehouse.dto.AisleCreateDTO;
import org.app.backend.modules.warehouse.dto.AisleResponseDTO;
import org.app.backend.modules.warehouse.dto.AisleUpdateDTO;
import org.app.backend.modules.warehouse.dto.FloorCreateDTO;
import org.app.backend.modules.warehouse.dto.FloorResponseDTO;
import org.app.backend.modules.warehouse.dto.FloorUpdateDTO;
import org.app.backend.modules.warehouse.entity.Aisle;
import org.app.backend.modules.warehouse.entity.Floor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/warehouse")
@Validated
public class WarehouseV1Controller {
  private static final Logger logger = LoggerFactory.getLogger(WarehouseV1Controller.class);
  private final WarehouseService warehouseService;

  public WarehouseV1Controller(WarehouseService warehouseService) {
    this.warehouseService = warehouseService;
  }

  // Floor APIs
  @PostMapping("/floors")
  public ResponseEntity<FloorResponseDTO> createFloor(@Valid @RequestBody FloorCreateDTO dto) {
    logger.info("Creating floor: {}", dto.getName());
    Floor floor = new Floor();
    floor.setName(dto.getName());
    floor.setStatus(dto.getStatus());
    Floor created = warehouseService.createFloor(floor);
    return ResponseEntity.status(HttpStatus.CREATED).body(mapToFloorResponseDTO(created));
  }

  @PutMapping("/floors/{id}")
  public ResponseEntity<FloorResponseDTO> updateFloor(
      @PathVariable UUID id, @Valid @RequestBody FloorUpdateDTO dto) {
    logger.info("Updating floor with id: {}", id);
    Floor floor = new Floor();
    floor.setName(dto.getName());
    floor.setStatus(dto.getStatus());
    Floor updated = warehouseService.updateFloor(id, floor);
    return ResponseEntity.ok(mapToFloorResponseDTO(updated));
  }

  @DeleteMapping("/floors/{id}")
  public ResponseEntity<Void> deleteFloor(@PathVariable UUID id) {
    logger.info("Deleting floor with id: {}", id);
    warehouseService.deleteFloor(id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/floors/{id}")
  public ResponseEntity<FloorResponseDTO> getFloorById(@PathVariable UUID id) {
    logger.debug("Getting floor with id: {}", id);
    Floor floor = warehouseService.getFloorById(id);
    return ResponseEntity.ok(mapToFloorResponseDTO(floor));
  }

  @GetMapping("/floors")
  public ResponseEntity<List<FloorResponseDTO>> getAllFloors() {
    logger.debug("Getting all floors");
    List<Floor> floors = warehouseService.getAllFloors();
    List<FloorResponseDTO> response =
        floors.stream().map(this::mapToFloorResponseDTO).collect(Collectors.toList());
    return ResponseEntity.ok(response);
  }

  // Aisle APIs
  @PostMapping("/aisles")
  public ResponseEntity<AisleResponseDTO> createAisle(@Valid @RequestBody AisleCreateDTO dto) {
    logger.info("Creating aisle: {} for floor: {}", dto.getName(), dto.getFloorId());
    Aisle aisle = new Aisle();
    Floor floor = warehouseService.getFloorById(dto.getFloorId());
    aisle.setFloor(floor);
    aisle.setName(dto.getName());
    aisle.setStatus(dto.getStatus());
    Aisle created = warehouseService.createAisle(aisle);
    return ResponseEntity.status(HttpStatus.CREATED).body(mapToAisleResponseDTO(created));
  }

  @PutMapping("/aisles/{id}")
  public ResponseEntity<AisleResponseDTO> updateAisle(
      @PathVariable UUID id, @Valid @RequestBody AisleUpdateDTO dto) {
    logger.info("Updating aisle with id: {}", id);
    Aisle aisle = new Aisle();
    if (dto.getFloorId() != null) {
      Floor floor = warehouseService.getFloorById(dto.getFloorId());
      aisle.setFloor(floor);
    }
    aisle.setName(dto.getName());
    aisle.setStatus(dto.getStatus());
    Aisle updated = warehouseService.updateAisle(id, aisle);
    return ResponseEntity.ok(mapToAisleResponseDTO(updated));
  }

  @DeleteMapping("/aisles/{id}")
  public ResponseEntity<Void> deleteAisle(@PathVariable UUID id) {
    logger.info("Deleting aisle with id: {}", id);
    warehouseService.deleteAisle(id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/aisles/{id}")
  public ResponseEntity<AisleResponseDTO> getAisleById(@PathVariable UUID id) {
    logger.debug("Getting aisle with id: {}", id);
    Aisle aisle = warehouseService.getAisleById(id);
    return ResponseEntity.ok(mapToAisleResponseDTO(aisle));
  }

  @GetMapping("/aisles")
  public ResponseEntity<List<AisleResponseDTO>> getAllAisles() {
    logger.debug("Getting all aisles");
    List<Aisle> aisles = warehouseService.getAllAisles();
    List<AisleResponseDTO> response =
        aisles.stream().map(this::mapToAisleResponseDTO).collect(Collectors.toList());
    return ResponseEntity.ok(response);
  }

  private FloorResponseDTO mapToFloorResponseDTO(Floor floor) {
    return FloorResponseDTO.builder()
        .id(floor.getId())
        .name(floor.getName())
        .status(floor.getStatus())
        .build();
  }

  private AisleResponseDTO mapToAisleResponseDTO(Aisle aisle) {
    return AisleResponseDTO.builder()
        .id(aisle.getId())
        .floorId(aisle.getFloor().getId())
        .floorName(aisle.getFloor().getName())
        .name(aisle.getName())
        .status(aisle.getStatus())
        .build();
  }
}
