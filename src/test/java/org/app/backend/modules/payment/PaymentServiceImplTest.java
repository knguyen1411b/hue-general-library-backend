package org.app.backend.modules.payment;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.UUID;
import org.app.backend.modules.auth.security.CustomUserDetails;
import org.app.backend.modules.payment.dto.PaymentCreateDTO;
import org.app.backend.modules.payment.dto.PaymentDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

  @Mock private PaymentRepository paymentRepository;
  @Mock private ModelMapper modelMapper;

  @InjectMocks private PaymentServiceImpl paymentService;

  private Payment mockPayment;
  private CustomUserDetails mockActor;
  private UUID paymentId;

  @BeforeEach
  void setUp() {
    paymentId = UUID.randomUUID();
    mockPayment = new Payment();
    mockPayment.setId(paymentId);
    mockPayment.setPaymentStatus(PaymentStatus.PENDING);

    mockActor = new CustomUserDetails();
    mockActor.setId(UUID.randomUUID());
    mockActor.setUsername("admin");
  }

  @Test
  @DisplayName("Find By Id - Success")
  void testFindById_Success() {
    PaymentDTO dto = new PaymentDTO();
    dto.setId(paymentId);

    when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(mockPayment));
    when(modelMapper.map(mockPayment, PaymentDTO.class)).thenReturn(dto);

    PaymentDTO result = paymentService.findById(paymentId);

    assertNotNull(result);
    assertEquals(paymentId, result.getId());
  }

  @Test
  @DisplayName("Find By Id - Not Found")
  void testFindById_NotFound() {
    when(paymentRepository.findById(paymentId)).thenReturn(Optional.empty());

    RuntimeException ex = assertThrows(RuntimeException.class,
        () -> paymentService.findById(paymentId));
    assertEquals("Payment not found", ex.getMessage());
  }

  @Test
  @DisplayName("Create Payment - Sets status PENDING")
  void testCreate_Success() {
    PaymentCreateDTO dto = new PaymentCreateDTO();
    Payment mappedPayment = new Payment();
    PaymentDTO resultDto = new PaymentDTO();

    when(modelMapper.map(dto, Payment.class)).thenReturn(mappedPayment);
    when(paymentRepository.save(mappedPayment)).thenReturn(mappedPayment);
    when(modelMapper.map(mappedPayment, PaymentDTO.class)).thenReturn(resultDto);

    PaymentDTO result = paymentService.create(dto, mockActor);

    assertNotNull(result);
    assertEquals(PaymentStatus.PENDING, mappedPayment.getPaymentStatus());
    verify(paymentRepository, times(1)).save(mappedPayment);
  }

  @Test
  @DisplayName("Confirm Payment - Changes status to SUCCESS")
  void testConfirm_Success() {
    PaymentDTO resultDto = new PaymentDTO();

    when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(mockPayment));
    when(paymentRepository.save(mockPayment)).thenReturn(mockPayment);
    when(modelMapper.map(mockPayment, PaymentDTO.class)).thenReturn(resultDto);

    paymentService.confirm(paymentId, mockActor);

    assertEquals(PaymentStatus.SUCCESS, mockPayment.getPaymentStatus());
    verify(paymentRepository, times(1)).save(mockPayment);
  }

  @Test
  @DisplayName("Delete Payment - Success")
  void testDelete_Success() {
    when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(mockPayment));

    paymentService.delete(paymentId, mockActor);

    verify(paymentRepository, times(1)).delete(mockPayment);
  }
}
