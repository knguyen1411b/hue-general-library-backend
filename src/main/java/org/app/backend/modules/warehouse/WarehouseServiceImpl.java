package org.app.backend.modules.warehouse;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.app.backend.common.exception.AppException;
import org.app.backend.modules.audit.AuditLogService;
import org.app.backend.modules.audit.enums.AuditLogAction;
import org.app.backend.modules.audit.enums.AuditLogEntity;
import org.app.backend.modules.audit.enums.AuditLogStatus;
import org.app.backend.modules.auth.security.CustomUserDetails;
import org.app.backend.modules.warehouse.dto.AddShelfDTO;
import org.app.backend.modules.warehouse.dto.AisleCreateDTO;
import org.app.backend.modules.warehouse.dto.AisleDTO;
import org.app.backend.modules.warehouse.dto.AisleUpdateDTO;
import org.app.backend.modules.warehouse.dto.FloorCreateDTO;
import org.app.backend.modules.warehouse.dto.FloorDTO;
import org.app.backend.modules.warehouse.dto.FloorUpdateDTO;
import org.app.backend.modules.warehouse.dto.PositionDTO;
import org.app.backend.modules.warehouse.dto.ShelfDTO;
import org.app.backend.modules.warehouse.dto.SimpleFloorDTO;
import org.app.backend.modules.warehouse.entity.Aisle;
import org.app.backend.modules.warehouse.entity.Floor;
import org.app.backend.modules.warehouse.entity.Position;
import org.app.backend.modules.warehouse.entity.Shelf;
import org.app.backend.modules.warehouse.repository.AisleRepository;
import org.app.backend.modules.warehouse.repository.FloorRepository;
import org.app.backend.modules.warehouse.repository.PositionRepository;
import org.app.backend.modules.warehouse.repository.ShelfRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class WarehouseServiceImpl implements WarehouseService {
  FloorRepository floorRepository;
  AisleRepository aisleRepository;
  ShelfRepository shelfRepository;
  PositionRepository positionRepository;
  ModelMapper modelMapper;
  AuditLogService auditLogService;

  @Override
  @Transactional(readOnly = true)
  @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
  public List<FloorDTO> getWarehouseTree() {
    List<Floor> floors = floorRepository.findAll(Sort.by("name").ascending());

    List<UUID> floorIds = floors.stream().map(Floor::getId).toList();

    List<Aisle> aisles = aisleRepository.findByFloorIdIn(floorIds);

    List<UUID> aisleIds = aisles.stream().map(Aisle::getId).toList();

    List<Shelf> shelves =
        aisleIds.isEmpty() ? List.of() : shelfRepository.findByAisleIdIn(aisleIds);

    Map<UUID, List<ShelfDTO>> shelvesByAisleId =
        shelves.stream().map(this::toShelfDTO).collect(Collectors.groupingBy(ShelfDTO::getAisleId));

    Map<UUID, List<AisleDTO>> aislesByFloorId =
        aisles.stream()
            .map(
                aisle -> {
                  AisleDTO dto = toAisleDTO(aisle);
                  dto.setShelves(shelvesByAisleId.getOrDefault(aisle.getId(), List.of()));
                  return dto;
                })
            .collect(Collectors.groupingBy(AisleDTO::getFloorId));

    return floors.stream()
        .map(
            floor -> {
              FloorDTO dto = modelMapper.map(floor, FloorDTO.class);
              dto.setAisles(aislesByFloorId.getOrDefault(floor.getId(), List.of()));
              return dto;
            })
        .toList();
  }

  @Override
  @Transactional
  @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
  public FloorDTO createFloor(FloorCreateDTO dto, CustomUserDetails actor) {
    validateFloorNameAvailability(dto.getName(), null);

    Floor floor = modelMapper.map(dto, Floor.class);
    floorRepository.save(floor);
    log(actor, AuditLogAction.CREATE, floor.getId().toString(), "Tạo tầng kho: " + floor.getName());
    return modelMapper.map(floor, FloorDTO.class);
  }

  @Override
  @Transactional
  @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
  public FloorDTO updateFloor(UUID id, FloorUpdateDTO dto, CustomUserDetails actor) {
    Floor floor = getFloorEntityOrThrow(id);

    if (dto.getName() != null && !dto.getName().isBlank()) {
      validateFloorNameAvailability(dto.getName(), floor.getName());
      floor.setName(dto.getName());
    }
    if (dto.getStatus() != null) {
      floor.setStatus(dto.getStatus());
    }

    floorRepository.save(floor);
    log(
        actor,
        AuditLogAction.UPDATE,
        floor.getId().toString(),
        "Cập nhật tầng kho: " + floor.getName());
    return modelMapper.map(floor, FloorDTO.class);
  }

  @Override
  @Transactional
  @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
  public void deleteFloor(UUID id, CustomUserDetails actor) {
    Floor floor = getFloorEntityOrThrow(id);
    floor.setStatus(FloorStatus.INACTIVE);
    floorRepository.save(floor);
    log(actor, AuditLogAction.DELETE, floor.getId().toString(), "Xóa tầng kho: " + floor.getName());
  }

  @Override
  @Transactional(readOnly = true)
  @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
  public FloorDTO getFloorById(UUID id) {
    return modelMapper.map(getFloorEntityOrThrow(id), FloorDTO.class);
  }

  @Override
  @Transactional(readOnly = true)
  @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
  public List<SimpleFloorDTO> getAllFloors() {
    return floorRepository.findAll().stream()
        .map(floor -> modelMapper.map(floor, SimpleFloorDTO.class))
        .toList();
  }

  @Override
  @Transactional
  @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
  public AisleDTO createAisle(AisleCreateDTO dto, CustomUserDetails actor) {
    Floor floor = getFloorEntityOrThrow(dto.getFloorId());
    validateAisleNameAvailability(dto.getFloorId(), dto.getName(), null);

    Aisle aisle = new Aisle();
    aisle.setFloor(floor);
    aisle.setName(dto.getName());
    aisle.setStatus(dto.getStatus());
    aisleRepository.save(aisle);

    log(actor, AuditLogAction.CREATE, aisle.getId().toString(), "Tạo dãy kệ: " + aisle.getName());
    return toAisleDTO(aisle);
  }

  @Override
  @Transactional
  @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
  public AisleDTO updateAisle(UUID id, AisleUpdateDTO dto, CustomUserDetails actor) {
    Aisle aisle = getAisleEntityOrThrow(id);

    if (dto.getFloorId() != null && !dto.getFloorId().equals(aisle.getFloor().getId())) {
      Floor newFloor = getFloorEntityOrThrow(dto.getFloorId());
      aisle.setFloor(newFloor);
    }

    if (dto.getName() != null && !dto.getName().isBlank()) {
      validateAisleNameAvailability(aisle.getFloor().getId(), dto.getName(), aisle.getName());
      aisle.setName(dto.getName());
    }

    if (dto.getStatus() != null) {
      aisle.setStatus(dto.getStatus());
    }

    aisleRepository.save(aisle);
    log(
        actor,
        AuditLogAction.UPDATE,
        aisle.getId().toString(),
        "Cập nhật dãy kệ: " + aisle.getName());
    return toAisleDTO(aisle);
  }

  @Override
  @Transactional
  @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
  public void deleteAisle(UUID id, CustomUserDetails actor) {
    Aisle aisle = getAisleEntityOrThrow(id);
    aisle.setStatus(AisleStatus.INACTIVE);
    aisleRepository.save(aisle);
    log(actor, AuditLogAction.DELETE, aisle.getId().toString(), "Xóa dãy kệ: " + aisle.getName());
  }

  @Override
  @Transactional(readOnly = true)
  @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
  public AisleDTO getAisleById(UUID id) {
    return toAisleDTO(getAisleEntityOrThrow(id));
  }

  @Override
  @Transactional(readOnly = true)
  @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
  public List<AisleDTO> getAllAisles() {
    return aisleRepository.findAll().stream().map(this::toAisleDTO).toList();
  }

  @Override
  @Transactional
  @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
  public ShelfDTO createShelf(AddShelfDTO dto, CustomUserDetails actor) {
    Aisle aisle = getAisleEntityOrThrow(dto.getAisleId());

    Shelf shelf = new Shelf();
    shelf.setAisle(aisle);
    shelf.setName(dto.getName());

    shelfRepository.save(shelf);
    log(actor, AuditLogAction.CREATE, shelf.getId().toString(), "Tạo kệ: " + shelf.getName());
    return toShelfDTO(shelf);
  }

  @Override
  @Transactional
  @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
  public void deleteShelf(UUID id, CustomUserDetails actor) {
    Shelf shelf =
        shelfRepository
            .findById(id)
            .orElseThrow(
                () ->
                    new AppException(
                        HttpStatus.NOT_FOUND, WarehouseMessage.SHELF_NOT_FOUND.getMessage()));
    shelfRepository.delete(shelf);
    log(actor, AuditLogAction.DELETE, shelf.getId().toString(), "Xóa kệ: " + shelf.getName());
  }

  private Floor getFloorEntityOrThrow(UUID id) {
    return floorRepository
        .findById(id)
        .orElseThrow(
            () ->
                new AppException(
                    HttpStatus.NOT_FOUND, WarehouseMessage.FLOOR_NOT_FOUND.getMessage()));
  }

  private Aisle getAisleEntityOrThrow(UUID id) {
    return aisleRepository
        .findById(id)
        .orElseThrow(
            () ->
                new AppException(
                    HttpStatus.NOT_FOUND, WarehouseMessage.AISLE_NOT_FOUND.getMessage()));
  }

  private void validateFloorNameAvailability(String name, String currentName) {
    if (name == null || name.isBlank() || name.equalsIgnoreCase(currentName)) {
      return;
    }
    if (floorRepository.existsByNameIgnoreCase(name)) {
      throw new AppException(HttpStatus.CONFLICT, WarehouseMessage.FLOOR_NAME_TAKEN.getMessage());
    }
  }

  private void validateAisleNameAvailability(UUID floorId, String name, String currentName) {
    if (name == null || name.isBlank() || name.equalsIgnoreCase(currentName)) {
      return;
    }
    if (aisleRepository.existsByFloorIdAndNameIgnoreCase(floorId, name)) {
      throw new AppException(HttpStatus.CONFLICT, WarehouseMessage.AISLE_NAME_TAKEN.getMessage());
    }
  }

  private AisleDTO toAisleDTO(Aisle aisle) {
    return AisleDTO.builder()
        .id(aisle.getId())
        .floorId(aisle.getFloor().getId())
        .floorName(aisle.getFloor().getName())
        .name(aisle.getName())
        .status(aisle.getStatus())
        .build();
  }

  private ShelfDTO toShelfDTO(Shelf shelf) {
    return ShelfDTO.builder()
        .id(shelf.getId())
        .name(shelf.getName())
        .aisleId(shelf.getAisle().getId())
        .build();
  }

  private PositionDTO toPositionDTO(Position position) {
    return PositionDTO.builder().id(position.getId()).bookCount(position.getBookCount()).build();
  }

  private void log(
      CustomUserDetails actor, AuditLogAction action, String entityId, String message) {
    auditLogService.log(
        actor != null ? actor.getId() : null,
        actor != null ? actor.getUsername() : "system",
        action,
        AuditLogEntity.WAREHOUSE,
        entityId,
        AuditLogStatus.SUCCESS,
        message);
  }

  @Override
  @Transactional(readOnly = true)
  @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
  public List<ShelfDTO> getAllShelves() {
    return shelfRepository.findAll().stream().map(this::toShelfDTO).toList();
  }

  @Override
  @Transactional(readOnly = true)
  @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
  public List<PositionDTO> getPositionsByShelfId(UUID shelfId) {
    if (!shelfRepository.existsById(shelfId)) {
      throw new AppException(HttpStatus.NOT_FOUND, WarehouseMessage.SHELF_NOT_FOUND.getMessage());
    }
    return positionRepository.findByShelfId(shelfId).stream().map(this::toPositionDTO).toList();
  }
}
