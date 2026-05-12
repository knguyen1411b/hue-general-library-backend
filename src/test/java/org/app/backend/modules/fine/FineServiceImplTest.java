package org.app.backend.modules.fine;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.UUID;
import org.app.backend.modules.auth.security.CustomUserDetails;
import org.app.backend.modules.fine.dto.FineDTO;
import org.app.backend.modules.fine.enums.FineStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

@ExtendWith(MockitoExtension.class)
class FineServiceImplTest {

  @Mock private FineRepository fineRepository;
  @Mock private ModelMapper modelMapper;

  @InjectMocks private FineServiceImpl fineService;

  private Fine mockFine;
  private CustomUserDetails mockUserDetails;
  private UUID fineId;

  @BeforeEach
  void setUp() {
    fineId = UUID.randomUUID();
    mockFine = new Fine();
    mockFine.setStatus(FineStatus.UNPAID);
    mockFine.setAmount(100);

    mockUserDetails = new CustomUserDetails();
    mockUserDetails.setId(UUID.randomUUID());
    mockUserDetails.setUsername("admin");
  }

  @Test
  @DisplayName("Find By Id - Success")
  void testFindById_Success() {
    FineDTO mockDto = new FineDTO();
    mockDto.setId(fineId);
    mockDto.setAmount(100.0);

    when(fineRepository.findById(fineId)).thenReturn(Optional.of(mockFine));
    when(modelMapper.map(mockFine, FineDTO.class)).thenReturn(mockDto);

    FineDTO result = fineService.findById(fineId);

    assertNotNull(result);
    assertEquals(100.0, result.getAmount());
  }

  @Test
  @DisplayName("Pay Fine - Success")
  void testPayFine_Success() {
    when(fineRepository.findById(fineId)).thenReturn(Optional.of(mockFine));
    when(fineRepository.save(mockFine)).thenReturn(mockFine);

    fineService.pay(fineId, mockUserDetails);

    assertEquals(FineStatus.PAID, mockFine.getStatus());
    verify(fineRepository, times(1)).save(mockFine);
  }

  @Test
  @DisplayName("Pay Fine - Not Found")
  void testPayFine_NotFound() {
    when(fineRepository.findById(fineId)).thenReturn(Optional.empty());

    RuntimeException exception =
        assertThrows(RuntimeException.class, () -> fineService.pay(fineId, mockUserDetails));
    assertEquals("Fine not found", exception.getMessage());
  }
}
