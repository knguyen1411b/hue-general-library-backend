package org.app.backend.modules.usersubscription;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.app.backend.common.exception.AppException;
import org.app.backend.modules.auth.security.CustomUserDetails;
import org.app.backend.modules.subscription.Subscription;
import org.app.backend.modules.subscription.SubscriptionRepository;
import org.app.backend.modules.user.User;
import org.app.backend.modules.user.UserRepository;
import org.app.backend.modules.user.UserRole;
import org.app.backend.modules.usersubscription.dto.UserSubscriptionCreateDTO;
import org.app.backend.modules.usersubscription.dto.UserSubscriptionResponseDTO;
import org.app.backend.modules.usersubscription.dto.UserSubscriptionUpdateDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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

  private UUID userId;
  private UUID otherUserId;
  private UUID subscriptionId;
  private User user;
  private User otherUser;
  private Subscription subscription;

  @BeforeEach
  void setUp() {
    userId = UUID.randomUUID();
    otherUserId = UUID.randomUUID();
    subscriptionId = UUID.randomUUID();

    user = new User();
    user.setId(userId);

    otherUser = new User();
    otherUser.setId(otherUserId);

    subscription = new Subscription();
    subscription.setId(subscriptionId);
    subscription.setDurationDays(30);
    subscription.setMaxBooks(5);
    subscription.setPrice(100000);
  }

  @Test
  @DisplayName("Create - success creates ACTIVE subscription from selected plan")
  void create_success() {
    UserSubscriptionCreateDTO dto = new UserSubscriptionCreateDTO(userId, subscriptionId);
    UserSubscriptionResponseDTO responseDTO = new UserSubscriptionResponseDTO();
    responseDTO.setId(UUID.randomUUID());

    when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    when(subscriptionRepository.findById(subscriptionId)).thenReturn(Optional.of(subscription));
    when(userSubscriptionRepository.existsByUser_IdAndStatus(userId, UserSubscriptionStatus.ACTIVE))
        .thenReturn(false);
    when(userSubscriptionRepository.save(any(UserSubscription.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));
    when(modelMapper.map(any(UserSubscription.class), eq(UserSubscriptionResponseDTO.class)))
        .thenReturn(responseDTO);

    UserSubscriptionResponseDTO result = userSubscriptionService.create(dto);

    assertSame(responseDTO, result);
    ArgumentCaptor<UserSubscription> captor = ArgumentCaptor.forClass(UserSubscription.class);
    verify(userSubscriptionRepository).save(captor.capture());
    UserSubscription saved = captor.getValue();
    assertEquals(user, saved.getUser());
    assertEquals(subscription, saved.getSubscription());
    assertEquals(UserSubscriptionStatus.ACTIVE, saved.getStatus());
    assertEquals(subscription.getMaxBooks(), saved.getMaxBooks());
    assertEquals(subscription.getPrice(), saved.getPrice());
  }

  @Test
  @DisplayName("Create - user not found throws AppException")
  void create_userNotFound_throwsException() {
    UserSubscriptionCreateDTO dto = new UserSubscriptionCreateDTO(userId, subscriptionId);
    when(userRepository.findById(userId)).thenReturn(Optional.empty());

    assertThrows(AppException.class, () -> userSubscriptionService.create(dto));
    verifyNoInteractions(subscriptionRepository, modelMapper);
  }

  @Test
  @DisplayName("Create - subscription not found throws AppException")
  void create_subscriptionNotFound_throwsException() {
    UserSubscriptionCreateDTO dto = new UserSubscriptionCreateDTO(userId, subscriptionId);
    when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    when(subscriptionRepository.findById(subscriptionId)).thenReturn(Optional.empty());

    assertThrows(AppException.class, () -> userSubscriptionService.create(dto));
  }

  @Test
  @DisplayName("Create - existing ACTIVE subscription throws AppException")
  void create_userAlreadyHasActive_throwsException() {
    UserSubscriptionCreateDTO dto = new UserSubscriptionCreateDTO(userId, subscriptionId);
    when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    when(subscriptionRepository.findById(subscriptionId)).thenReturn(Optional.of(subscription));
    when(userSubscriptionRepository.existsByUser_IdAndStatus(userId, UserSubscriptionStatus.ACTIVE))
        .thenReturn(true);

    assertThrows(AppException.class, () -> userSubscriptionService.create(dto));
    verify(userSubscriptionRepository, never()).save(any());
  }

  @Test
  @DisplayName("Get all - excludes soft deleted subscriptions")
  void getAll_excludesDeleted() {
    UserSubscription entity = activeEntity(UUID.randomUUID(), user);
    UserSubscriptionResponseDTO dto = response(entity.getId());

    when(userSubscriptionRepository.findByStatusNot(UserSubscriptionStatus.DELETED))
        .thenReturn(List.of(entity));
    when(modelMapper.map(entity, UserSubscriptionResponseDTO.class)).thenReturn(dto);

    List<UserSubscriptionResponseDTO> result = userSubscriptionService.getAll();

    assertEquals(List.of(dto), result);
    verify(userSubscriptionRepository).findByStatusNot(UserSubscriptionStatus.DELETED);
    verify(userSubscriptionRepository, never()).findAll();
  }

  @Test
  @DisplayName("Get by user id - excludes soft deleted subscriptions")
  void getByUserId_excludesDeleted() {
    UserSubscription entity = activeEntity(UUID.randomUUID(), user);
    UserSubscriptionResponseDTO dto = response(entity.getId());

    when(userSubscriptionRepository.findByUser_IdAndStatusNot(userId, UserSubscriptionStatus.DELETED))
        .thenReturn(List.of(entity));
    when(modelMapper.map(entity, UserSubscriptionResponseDTO.class)).thenReturn(dto);

    List<UserSubscriptionResponseDTO> result = userSubscriptionService.getByUserId(userId);

    assertEquals(List.of(dto), result);
    verify(userSubscriptionRepository)
        .findByUser_IdAndStatusNot(userId, UserSubscriptionStatus.DELETED);
  }

  @Test
  @DisplayName("Get by id - DELETED subscription behaves as not found")
  void getById_deleted_throwsNotFound() {
    UUID entityId = UUID.randomUUID();
    UserSubscription entity = activeEntity(entityId, user);
    entity.setStatus(UserSubscriptionStatus.DELETED);

    when(userSubscriptionRepository.findById(entityId)).thenReturn(Optional.of(entity));

    assertThrows(AppException.class, () -> userSubscriptionService.getById(entityId));
    verifyNoInteractions(modelMapper);
  }

  @Test
  @DisplayName("Update - ADMIN can update allowed DTO fields")
  void update_admin_success() {
    UUID entityId = UUID.randomUUID();
    UserSubscription entity = activeEntity(entityId, user);
    LocalDate newStartDate = LocalDate.now().plusDays(1);
    LocalDate newEndDate = LocalDate.now().plusDays(45);
    UserSubscriptionUpdateDTO updateDTO =
        UserSubscriptionUpdateDTO.builder()
            .startDate(newStartDate)
            .endDate(newEndDate)
            .status(UserSubscriptionStatus.EXPIRED)
            .build();
    UserSubscriptionResponseDTO responseDTO = response(entityId);

    when(userSubscriptionRepository.findById(entityId)).thenReturn(Optional.of(entity));
    when(userSubscriptionRepository.save(entity)).thenReturn(entity);
    when(modelMapper.map(entity, UserSubscriptionResponseDTO.class)).thenReturn(responseDTO);

    UserSubscriptionResponseDTO result =
        userSubscriptionService.update(entityId, updateDTO, actor(userId, UserRole.ADMIN));

    assertSame(responseDTO, result);
    assertEquals(newStartDate, entity.getStartDate());
    assertEquals(newEndDate, entity.getEndDate());
    assertEquals(UserSubscriptionStatus.EXPIRED, entity.getStatus());
  }

  @Test
  @DisplayName("Update - USER can cancel own subscription")
  void update_userCanCancelOwnSubscription() {
    UUID entityId = UUID.randomUUID();
    UserSubscription entity = activeEntity(entityId, user);
    UserSubscriptionUpdateDTO updateDTO =
        UserSubscriptionUpdateDTO.builder().status(UserSubscriptionStatus.CANCELED).build();
    UserSubscriptionResponseDTO responseDTO = response(entityId);

    when(userSubscriptionRepository.findById(entityId)).thenReturn(Optional.of(entity));
    when(userSubscriptionRepository.save(entity)).thenReturn(entity);
    when(modelMapper.map(entity, UserSubscriptionResponseDTO.class)).thenReturn(responseDTO);

    UserSubscriptionResponseDTO result =
        userSubscriptionService.update(entityId, updateDTO, actor(userId, UserRole.USER));

    assertSame(responseDTO, result);
    assertEquals(UserSubscriptionStatus.CANCELED, entity.getStatus());
  }

  @Test
  @DisplayName("Update - USER can renew own subscription with later endDate")
  void update_userCanRenewOwnSubscription() {
    UUID entityId = UUID.randomUUID();
    UserSubscription entity = activeEntity(entityId, user);
    LocalDate renewedEndDate = entity.getEndDate().plusDays(30);
    UserSubscriptionUpdateDTO updateDTO =
        UserSubscriptionUpdateDTO.builder().endDate(renewedEndDate).build();
    UserSubscriptionResponseDTO responseDTO = response(entityId);

    when(userSubscriptionRepository.findById(entityId)).thenReturn(Optional.of(entity));
    when(userSubscriptionRepository.save(entity)).thenReturn(entity);
    when(modelMapper.map(entity, UserSubscriptionResponseDTO.class)).thenReturn(responseDTO);

    UserSubscriptionResponseDTO result =
        userSubscriptionService.update(entityId, updateDTO, actor(userId, UserRole.USER));

    assertSame(responseDTO, result);
    assertEquals(renewedEndDate, entity.getEndDate());
  }

  @Test
  @DisplayName("Update - USER cannot update another user's subscription")
  void update_userCannotUpdateOtherUserSubscription() {
    UUID entityId = UUID.randomUUID();
    UserSubscription entity = activeEntity(entityId, otherUser);
    UserSubscriptionUpdateDTO updateDTO =
        UserSubscriptionUpdateDTO.builder().status(UserSubscriptionStatus.CANCELED).build();

    when(userSubscriptionRepository.findById(entityId)).thenReturn(Optional.of(entity));

    assertThrows(
        AppException.class,
        () -> userSubscriptionService.update(entityId, updateDTO, actor(userId, UserRole.USER)));
    verify(userSubscriptionRepository, never()).save(any());
  }

  @Test
  @DisplayName("Update - USER cannot update startDate")
  void update_userCannotUpdateStartDate() {
    UUID entityId = UUID.randomUUID();
    UserSubscription entity = activeEntity(entityId, user);
    UserSubscriptionUpdateDTO updateDTO =
        UserSubscriptionUpdateDTO.builder().startDate(LocalDate.now().plusDays(1)).build();

    when(userSubscriptionRepository.findById(entityId)).thenReturn(Optional.of(entity));

    assertThrows(
        AppException.class,
        () -> userSubscriptionService.update(entityId, updateDTO, actor(userId, UserRole.USER)));
    verify(userSubscriptionRepository, never()).save(any());
  }

  @Test
  @DisplayName("Update - USER cannot set status ACTIVE")
  void update_userCannotSetActiveStatus() {
    UUID entityId = UUID.randomUUID();
    UserSubscription entity = activeEntity(entityId, user);
    UserSubscriptionUpdateDTO updateDTO =
        UserSubscriptionUpdateDTO.builder().status(UserSubscriptionStatus.ACTIVE).build();

    when(userSubscriptionRepository.findById(entityId)).thenReturn(Optional.of(entity));

    assertThrows(
        AppException.class,
        () -> userSubscriptionService.update(entityId, updateDTO, actor(userId, UserRole.USER)));
    verify(userSubscriptionRepository, never()).save(any());
  }

  @Test
  @DisplayName("Update - USER cannot renew to past endDate")
  void update_userCannotRenewToPastEndDate() {
    UUID entityId = UUID.randomUUID();
    UserSubscription entity = activeEntity(entityId, user);
    UserSubscriptionUpdateDTO updateDTO =
        UserSubscriptionUpdateDTO.builder().endDate(LocalDate.now().minusDays(1)).build();

    when(userSubscriptionRepository.findById(entityId)).thenReturn(Optional.of(entity));

    assertThrows(
        AppException.class,
        () -> userSubscriptionService.update(entityId, updateDTO, actor(userId, UserRole.USER)));
    verify(userSubscriptionRepository, never()).save(any());
  }

  @Test
  @DisplayName("Update - not found throws AppException")
  void update_notFound_throwsException() {
    UUID entityId = UUID.randomUUID();
    when(userSubscriptionRepository.findById(entityId)).thenReturn(Optional.empty());

    assertThrows(
        AppException.class,
        () ->
            userSubscriptionService.update(
                entityId,
                UserSubscriptionUpdateDTO.builder().status(UserSubscriptionStatus.CANCELED).build(),
                actor(userId, UserRole.ADMIN)));
  }

  @Test
  @DisplayName("Delete - soft deletes by setting status DELETED")
  void delete_success_softDeletes() {
    UUID entityId = UUID.randomUUID();
    UserSubscription entity = activeEntity(entityId, user);

    when(userSubscriptionRepository.findById(entityId)).thenReturn(Optional.of(entity));
    when(userSubscriptionRepository.save(entity)).thenReturn(entity);

    userSubscriptionService.delete(entityId);

    assertEquals(UserSubscriptionStatus.DELETED, entity.getStatus());
    verify(userSubscriptionRepository).save(entity);
    verify(userSubscriptionRepository, never()).deleteById(any());
  }

  @Test
  @DisplayName("Delete - already DELETED is idempotent")
  void delete_alreadyDeleted_doesNothing() {
    UUID entityId = UUID.randomUUID();
    UserSubscription entity = activeEntity(entityId, user);
    entity.setStatus(UserSubscriptionStatus.DELETED);

    when(userSubscriptionRepository.findById(entityId)).thenReturn(Optional.of(entity));

    userSubscriptionService.delete(entityId);

    verify(userSubscriptionRepository, never()).save(any());
    verify(userSubscriptionRepository, never()).deleteById(any());
  }

  @Test
  @DisplayName("Delete - not found throws AppException")
  void delete_notFound_throwsException() {
    UUID entityId = UUID.randomUUID();
    when(userSubscriptionRepository.findById(entityId)).thenReturn(Optional.empty());

    assertThrows(AppException.class, () -> userSubscriptionService.delete(entityId));
    verify(userSubscriptionRepository, never()).deleteById(any());
  }

  private UserSubscription activeEntity(UUID id, User owner) {
    UserSubscription entity = new UserSubscription();
    entity.setId(id);
    entity.setUser(owner);
    entity.setSubscription(subscription);
    entity.setStartDate(LocalDate.now());
    entity.setEndDate(LocalDate.now().plusDays(30));
    entity.setStatus(UserSubscriptionStatus.ACTIVE);
    entity.setMaxBooks(subscription.getMaxBooks());
    entity.setPrice(subscription.getPrice());
    return entity;
  }

  private UserSubscriptionResponseDTO response(UUID id) {
    UserSubscriptionResponseDTO dto = new UserSubscriptionResponseDTO();
    dto.setId(id);
    return dto;
  }

  private CustomUserDetails actor(UUID id, UserRole role) {
    return CustomUserDetails.builder().id(id).role(role).build();
  }
}
