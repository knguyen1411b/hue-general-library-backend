package org.app.backend.modules.usersubscription;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import org.app.backend.common.exception.AppException;
import org.app.backend.modules.subscription.Subscription;
import org.app.backend.modules.user.User;
import org.app.backend.modules.usersubscription.dto.UserSubscriptionResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

@ExtendWith(MockitoExtension.class)
class UserSubscriptionServiceImplTest {

  @Mock private UserSubscriptionRepository userSubscriptionRepository;
  @Mock private ModelMapper modelMapper;

  @InjectMocks private UserSubscriptionServiceImpl userSubscriptionService;

  private UserSubscription mockEntity;
  private UUID entityId;

  @BeforeEach
  void setUp() {
    entityId = UUID.randomUUID();
    User user = new User();
    user.setId(UUID.randomUUID());
    Subscription subscription = new Subscription();
    subscription.setId(UUID.randomUUID());
    mockEntity = new UserSubscription();
    mockEntity.setId(entityId);
    mockEntity.setUser(user);
    mockEntity.setSubscription(subscription);
    mockEntity.setStartDate(LocalDate.now());
    mockEntity.setEndDate(LocalDate.now().plusDays(30));
    mockEntity.setStatus(UserSubscriptionStatus.ACTIVE);
  }

  @Test
  @DisplayName("Get By Id - Success")
  void testGetById_Success() {
    UserSubscriptionResponseDTO dto = new UserSubscriptionResponseDTO();
    dto.setId(entityId);
    when(userSubscriptionRepository.findById(entityId)).thenReturn(Optional.of(mockEntity));
    when(modelMapper.map(mockEntity, UserSubscriptionResponseDTO.class)).thenReturn(dto);
    UserSubscriptionResponseDTO result = userSubscriptionService.getById(entityId);
    assertNotNull(result);
    assertEquals(entityId, result.getId());
  }

  @Test
  @DisplayName("Get By Id - Not Found")
  void testGetById_NotFound() {
    when(userSubscriptionRepository.findById(entityId)).thenReturn(Optional.empty());
    assertThrows(AppException.class, () -> userSubscriptionService.getById(entityId));
  }

  @Test
  @DisplayName("Activate Subscription - Sets status ACTIVE")
  void testActivateSubscription_Success() {
    mockEntity.setStatus(UserSubscriptionStatus.EXPIRED); // start from a non-ACTIVE state
    UserSubscriptionResponseDTO dto = new UserSubscriptionResponseDTO();
    when(userSubscriptionRepository.findById(entityId)).thenReturn(Optional.of(mockEntity));
    when(userSubscriptionRepository.save(mockEntity)).thenReturn(mockEntity);
    when(modelMapper.map(mockEntity, UserSubscriptionResponseDTO.class)).thenReturn(dto);
    userSubscriptionService.activateSubscription(entityId);
    assertEquals(UserSubscriptionStatus.ACTIVE, mockEntity.getStatus());
  }

  @Test
  @DisplayName("Cancel Subscription - Sets status CANCELED")
  void testCancelSubscription_Success() {
    UserSubscriptionResponseDTO dto = new UserSubscriptionResponseDTO();
    when(userSubscriptionRepository.findById(entityId)).thenReturn(Optional.of(mockEntity));
    when(userSubscriptionRepository.save(mockEntity)).thenReturn(mockEntity);
    when(modelMapper.map(mockEntity, UserSubscriptionResponseDTO.class)).thenReturn(dto);
    userSubscriptionService.cancelSubscription(entityId);
    assertEquals(UserSubscriptionStatus.CANCELED, mockEntity.getStatus());
  }

  @Test
  @DisplayName("Renew - End before Start throws exception")
  void testRenew_InvalidDates_ThrowsException() {
    LocalDate start = LocalDate.now().plusDays(10);
    LocalDate end = LocalDate.now();
    assertThrows(AppException.class,
        () -> userSubscriptionService.renewSubscription(entityId, start, end));
  }

  @Test
  @DisplayName("Delete - Not Found throws exception")
  void testDelete_NotFound() {
    when(userSubscriptionRepository.existsById(entityId)).thenReturn(false);
    assertThrows(AppException.class, () -> userSubscriptionService.delete(entityId));
  }

  @Test
  @DisplayName("Delete - Success")
  void testDelete_Success() {
    when(userSubscriptionRepository.existsById(entityId)).thenReturn(true);
    userSubscriptionService.delete(entityId);
    verify(userSubscriptionRepository, times(1)).deleteById(entityId);
  }
}
