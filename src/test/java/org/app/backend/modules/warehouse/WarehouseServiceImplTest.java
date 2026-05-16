package org.app.backend.modules.warehouse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.app.backend.common.exception.AppException;
import org.app.backend.modules.audit.AuditLogService;
import org.app.backend.modules.auth.security.CustomUserDetails;
import org.app.backend.modules.warehouse.dto.AisleCreateDTO;
import org.app.backend.modules.warehouse.dto.AisleDTO;
import org.app.backend.modules.warehouse.dto.FloorCreateDTO;
import org.app.backend.modules.warehouse.dto.FloorDTO;
import org.app.backend.modules.warehouse.dto.PositionDTO;
import org.app.backend.modules.warehouse.entity.Aisle;
import org.app.backend.modules.warehouse.entity.Floor;
import org.app.backend.modules.warehouse.entity.Position;
import org.app.backend.modules.warehouse.repository.AisleRepository;
import org.app.backend.modules.warehouse.repository.FloorRepository;
import org.app.backend.modules.warehouse.repository.PositionRepository;
import org.app.backend.modules.warehouse.repository.ShelfRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

@ExtendWith(MockitoExtension.class)
class WarehouseServiceImplTest {

  @Mock private FloorRepository floorRepository;
  @Mock private AisleRepository aisleRepository;
  @Mock private ShelfRepository shelfRepository;
  @Mock private PositionRepository positionRepository;
  @Mock private ModelMapper modelMapper;
  @Mock private AuditLogService auditLogService;

  @InjectMocks private WarehouseServiceImpl warehouseService;

  private Floor mockFloor;
  private Aisle mockAisle;
  private CustomUserDetails mockActor;
  private UUID floorId;
  private UUID aisleId;
  private UUID shelfId;

  @BeforeEach
  void setUp() {
    floorId = UUID.randomUUID();
    aisleId = UUID.randomUUID();
    shelfId = UUID.randomUUID();

    mockFloor = new Floor();
    mockFloor.setId(floorId);
    mockFloor.setName("Floor 1");
    mockFloor.setStatus(FloorStatus.ACTIVE);

    mockAisle = new Aisle();
    mockAisle.setId(aisleId);
    mockAisle.setName("Aisle A");
    mockAisle.setFloor(mockFloor);

    mockActor = new CustomUserDetails();
    mockActor.setId(UUID.randomUUID());
    mockActor.setUsername("admin");
  }

  @Test
  @DisplayName("Get Floor By Id - Success")
  void testGetFloorById_Success() {
    FloorDTO dto = new FloorDTO();
    dto.setId(floorId);
    when(floorRepository.findById(floorId)).thenReturn(Optional.of(mockFloor));
    when(modelMapper.map(mockFloor, FloorDTO.class)).thenReturn(dto);

    FloorDTO result = warehouseService.getFloorById(floorId);

    assertNotNull(result);
    assertEquals(floorId, result.getId());
  }

  @Test
  @DisplayName("Get Floor By Id - Not Found")
  void testGetFloorById_NotFound() {
    when(floorRepository.findById(floorId)).thenReturn(Optional.empty());
    assertThrows(AppException.class, () -> warehouseService.getFloorById(floorId));
  }

  @Test
  @DisplayName("Create Floor - Success")
  void testCreateFloor_Success() {
    FloorCreateDTO dto = new FloorCreateDTO();
    dto.setName("Floor 2");
    Floor mappedFloor = new Floor();
    mappedFloor.setId(UUID.randomUUID());
    mappedFloor.setName("Floor 2");
    FloorDTO resultDto = new FloorDTO();

    when(floorRepository.existsByNameIgnoreCase("Floor 2")).thenReturn(false);
    when(modelMapper.map(dto, Floor.class)).thenReturn(mappedFloor);
    when(floorRepository.save(mappedFloor)).thenReturn(mappedFloor);
    when(modelMapper.map(mappedFloor, FloorDTO.class)).thenReturn(resultDto);

    FloorDTO result = warehouseService.createFloor(dto, mockActor);

    assertNotNull(result);
    verify(floorRepository, times(1)).save(mappedFloor);
    verify(auditLogService, times(1)).log(any(), any(), any(), any(), any(), any(), anyString());
  }

  @Test
  @DisplayName("Create Floor - Duplicate name throws CONFLICT")
  void testCreateFloor_DuplicateName_ThrowsConflict() {
    FloorCreateDTO dto = new FloorCreateDTO();
    dto.setName("Floor 1");
    when(floorRepository.existsByNameIgnoreCase("Floor 1")).thenReturn(true);

    assertThrows(AppException.class, () -> warehouseService.createFloor(dto, mockActor));
    verify(floorRepository, never()).save(any());
  }

  @Test
  @DisplayName("Delete Floor - Sets status INACTIVE")
  void testDeleteFloor_Success() {
    when(floorRepository.findById(floorId)).thenReturn(Optional.of(mockFloor));

    warehouseService.deleteFloor(floorId, mockActor);

    assertEquals(FloorStatus.INACTIVE, mockFloor.getStatus());
    verify(floorRepository, times(1)).save(mockFloor);
  }

  @Test
  @DisplayName("Create Aisle - Success")
  void testCreateAisle_Success() {
    AisleCreateDTO dto = new AisleCreateDTO();
    dto.setFloorId(floorId);
    dto.setName("Aisle B");
    dto.setStatus(AisleStatus.ACTIVE);

    when(floorRepository.findById(floorId)).thenReturn(Optional.of(mockFloor));
    when(aisleRepository.existsByFloorIdAndNameIgnoreCase(floorId, "Aisle B")).thenReturn(false);
    when(aisleRepository.save(any(Aisle.class)))
        .thenAnswer(
            inv -> {
              Aisle a = inv.getArgument(0);
              a.setId(UUID.randomUUID());
              return a;
            });

    AisleDTO result = warehouseService.createAisle(dto, mockActor);

    assertNotNull(result);
    verify(aisleRepository, times(1)).save(any(Aisle.class));
  }

  @Test
  @DisplayName("Delete Aisle - Sets status INACTIVE")
  void testDeleteAisle_Success() {
    when(aisleRepository.findById(aisleId)).thenReturn(Optional.of(mockAisle));

    warehouseService.deleteAisle(aisleId, mockActor);

    assertEquals(AisleStatus.INACTIVE, mockAisle.getStatus());
    verify(aisleRepository, times(1)).save(mockAisle);
  }

  @Test
  @DisplayName("Get Positions By Shelf Id - Success")
  void testGetPositionsByShelfId_Success() {
    Position position1 = new Position();
    position1.setId(UUID.randomUUID());
    position1.setBookCount(2);

    Position position2 = new Position();
    position2.setId(UUID.randomUUID());
    position2.setBookCount(0);

    when(shelfRepository.existsById(shelfId)).thenReturn(true);
    when(positionRepository.findByShelfId(shelfId)).thenReturn(List.of(position1, position2));

    List<PositionDTO> result = warehouseService.getPositionsByShelfId(shelfId);

    assertEquals(2, result.size());
    assertEquals(position1.getId(), result.get(0).getId());
    assertEquals(2, result.get(0).getBookCount());
    verify(positionRepository, times(1)).findByShelfId(shelfId);
  }

  @Test
  @DisplayName("Get Positions By Shelf Id - Shelf Not Found")
  void testGetPositionsByShelfId_ShelfNotFound() {
    when(shelfRepository.existsById(shelfId)).thenReturn(false);

    assertThrows(AppException.class, () -> warehouseService.getPositionsByShelfId(shelfId));
    verify(positionRepository, never()).findByShelfId(any());
  }
}
