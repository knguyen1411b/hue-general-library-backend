package org.app.backend.modules.usersubscription;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import org.app.backend.common.exception.AppException;
import org.app.backend.modules.subscription.Subscription;
import org.app.backend.modules.user.User;
import org.app.backend.modules.usersubscription.dto.UserSubscriptionCreateDTO;
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
  @Mock private SubscriptionRepository subscriptionRepository;
  @Mock private UserRepository userRepository;
  @Mock private ModelMapper modelMapper;

  @InjectMocks private UserSubscriptionServiceImpl userSubscriptionService;

  private User mockUser;
  private Subscription mockSubscription;
  private UUID userId;
  private UUID subscriptionId;

  @BeforeEach
  void setUp() {
    userId = UUID.randomUUID();
    subscriptionId = UUID.randomUUID();

    mockUser = new User();
    mockUser.setId(userId);

    mockSubscription = new Subscription();
    mockSubscription.setId(subscriptionId);
    mockSubscription.setDurationDays(30);
    mockSubscription.setMaxBooks(5);
    mockSubscription.setPrice(100000);
  }

  @Test
  @DisplayName("Create - Success")
  void testCreate_Success() {
    UserSubscriptionCreateDTO dto = new UserSubscriptionCreateDTO(userId, subscriptionId);
    UserSubscriptionResponseDTO responseDTO = new UserSubscriptionResponseDTO();
    responseDTO.setId(UUID.randomUUID());

    when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
    when(subscriptionRepository.findById(subscriptionId)).thenReturn(Optional.of(mockSubscription));
    when(userSubscriptionRepository.existsByUserIdAndStatus(userId, UserSubscriptionStatus.ACTIVE))
        .thenReturn(false);
    when(modelMapper.map(any(UserSubscription.class), eq(UserSubscriptionResponseDTO.class)))
        .thenReturn(responseDTO);

    UserSubscriptionResponseDTO result = userSubscriptionService.create(dto);

    assertNotNull(result);
    verify(userRepository).findById(userId);
    verify(subscriptionRepository).findById(subscriptionId);
  }

  @Test
  @DisplayName("Create - User not found throws exception")
  void testCreate_UserNotFound_ThrowsException() {
    UserSubscriptionCreateDTO dto = new UserSubscriptionCreateDTO(userId, subscriptionId);

    when(userRepository.findById(userId)).thenReturn(Optional.empty());

    assertThrows(AppException.class, () -> userSubscriptionService.create(dto));
  }

  @Test
  @DisplayName("Create - Subscription not found throws exception")
  void testCreate_SubscriptionNotFound_ThrowsException() {
    UserSubscriptionCreateDTO dto = new UserSubscriptionCreateDTO(userId, subscriptionId);

    when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
    when(subscriptionRepository.findById(subscriptionId)).thenReturn(Optional.empty());

    assertThrows(AppException.class, () -> userSubscriptionService.create(dto));
  }

  @Test
  @DisplayName("Create - User already has active subscription throws exception")
  void testCreate_UserAlreadyHasActive_ThrowsException() {
    UserSubscriptionCreateDTO dto = new UserSubscriptionCreateDTO(userId, subscriptionId);

    when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
    when(subscriptionRepository.findById(subscriptionId)).thenReturn(Optional.of(mockSubscription));
    when(userSubscriptionRepository.existsByUserIdAndStatus(userId, UserSubscriptionStatus.ACTIVE))
        .thenReturn(true);

    assertThrows(AppException.class, () -> userSubscriptionService.create(dto));
  }

  @Test
  @DisplayName("Get By Id - Success")
  void testGetById_Success() {
    UUID entityId = UUID.randomUUID();
    UserSubscription mockEntity = new UserSubscription();
    mockEntity.setId(entityId);
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
    UUID entityId = UUID.randomUUID();
    when(userSubscriptionRepository.findById(entityId)).thenReturn(Optional.empty());
    assertThrows(AppException.class, () -> userSubscriptionService.getById(entityId));
  }

  @Test
  @DisplayName("Update - Success")
  void testUpdate_Success() {
    UUID entityId = UUID.randomUUID();
    UserSubscription mockEntity = new UserSubscription();
    mockEntity.setId(entityId);
    mockEntity.setUser(mockUser);
    mockEntity.setSubscription(mockSubscription);
    mockEntity.setStartDate(LocalDate.now());
    mockEntity.setEndDate(LocalDate.now().plusDays(30));
    mockEntity.setStatus(UserSubscriptionStatus.ACTIVE);

    UserSubscription updateData = new UserSubscription();
    updateData.setStatus(UserSubscriptionStatus.CANCELED);

    UserSubscriptionResponseDTO dto = new UserSubscriptionResponseDTO();
    dto.setId(entityId);

    when(userSubscriptionRepository.findById(entityId)).thenReturn(Optional.of(mockEntity));
    when(userSubscriptionRepository.save(any(UserSubscription.class))).thenReturn(mockEntity);
    when(modelMapper.map(mockEntity, UserSubscriptionResponseDTO.class)).thenReturn(dto);

    UserSubscriptionResponseDTO result = userSubscriptionService.update(entityId, updateData);
    assertNotNull(result);
  }

  @Test
  @DisplayName("Update - Not Found throws exception")
  void testUpdate_NotFound() {
    UUID entityId = UUID.randomUUID();
    UserSubscription updateData = new UserSubscription();

    when(userSubscriptionRepository.findById(entityId)).thenReturn(Optional.empty());
    assertThrows(AppException.class, () -> userSubscriptionService.update(entityId, updateData));
  }

  @Test
  @DisplayName("Delete - Not Found throws exception")
  void testDelete_NotFound() {
    UUID entityId = UUID.randomUUID();
    when(userSubscriptionRepository.findById(entityId)).thenReturn(Optional.empty());
    assertThrows(AppException.class, () -> userSubscriptionService.delete(entityId));
  }

  @Test
  @DisplayName("Delete - Success")
  void testDelete_Success() {
    UUID entityId = UUID.randomUUID();
    UserSubscription mockEntity = new UserSubscription();
    mockEntity.setId(entityId);

    when(userSubscriptionRepository.findById(entityId)).thenReturn(Optional.of(mockEntity));
    userSubscriptionService.delete(entityId);
    verify(userSubscriptionRepository, times(1)).deleteById(entityId);
  }
}