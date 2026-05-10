package org.app.backend.modules.subscription;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.UUID;
import org.app.backend.common.exception.AppException;
import org.app.backend.modules.audit.AuditLogService;
import org.app.backend.modules.audit.enums.AuditLogAction;
import org.app.backend.modules.audit.enums.AuditLogEntity;
import org.app.backend.modules.audit.enums.AuditLogStatus;
import org.app.backend.modules.auth.security.CustomUserDetails;
import org.app.backend.modules.subscription.dto.SubscriptionCreateDTO;
import org.app.backend.modules.subscription.dto.SubscriptionDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceImplTest {

  @Mock private SubscriptionRepository subscriptionRepository;
  @Mock private ModelMapper modelMapper;
  @Mock private AuditLogService auditLogService;

  @InjectMocks private SubscriptionServiceImpl subscriptionService;

  private Subscription mockSubscription;
  private CustomUserDetails mockActor;
  private UUID subscriptionId;

  @BeforeEach
  void setUp() {
    subscriptionId = UUID.randomUUID();
    mockSubscription = new Subscription();
    mockSubscription.setId(subscriptionId);
    mockSubscription.setKey("BASIC");
    mockSubscription.setName("Basic Plan");
    mockSubscription.setStatus(SubscriptionStatus.ACTIVE);

    mockActor = new CustomUserDetails();
    mockActor.setId(UUID.randomUUID());
    mockActor.setUsername("admin");
  }

  @Test
  @DisplayName("Find By Id - Success")
  void testFindById_Success() {
    SubscriptionDTO dto = new SubscriptionDTO();
    dto.setId(subscriptionId);

    when(subscriptionRepository.findById(subscriptionId)).thenReturn(Optional.of(mockSubscription));
    when(modelMapper.map(mockSubscription, SubscriptionDTO.class)).thenReturn(dto);

    SubscriptionDTO result = subscriptionService.findById(subscriptionId);

    assertNotNull(result);
    assertEquals(subscriptionId, result.getId());
  }

  @Test
  @DisplayName("Find By Id - Not Found")
  void testFindById_NotFound() {
    when(subscriptionRepository.findById(subscriptionId)).thenReturn(Optional.empty());

    assertThrows(AppException.class, () -> subscriptionService.findById(subscriptionId));
  }

  @Test
  @DisplayName("Create Subscription - Success")
  void testCreate_Success() {
    SubscriptionCreateDTO dto = new SubscriptionCreateDTO();
    dto.setKey("premium");
    dto.setName("Premium Plan");

    Subscription mappedSub = new Subscription();
    mappedSub.setId(UUID.randomUUID());

    when(subscriptionRepository.existsByKey("PREMIUM")).thenReturn(false);
    when(subscriptionRepository.existsByName("Premium Plan")).thenReturn(false);
    when(modelMapper.map(dto, Subscription.class)).thenReturn(mappedSub);
    when(subscriptionRepository.save(mappedSub)).thenReturn(mappedSub);

    subscriptionService.create(dto, mockActor);

    assertEquals("PREMIUM", mappedSub.getKey());
    verify(subscriptionRepository, times(1)).save(mappedSub);
    verify(auditLogService, times(1)).log(
        eq(mockActor.getId()), eq(mockActor.getUsername()),
        eq(AuditLogAction.CREATE), eq(AuditLogEntity.SUBSCRIPTION),
        anyString(), eq(AuditLogStatus.SUCCESS), anyString());
  }

  @Test
  @DisplayName("Create Subscription - Duplicate key throws CONFLICT")
  void testCreate_DuplicateKey_ThrowsConflict() {
    SubscriptionCreateDTO dto = new SubscriptionCreateDTO();
    dto.setKey("basic");
    dto.setName("New Plan");

    when(subscriptionRepository.existsByKey("BASIC")).thenReturn(true);

    assertThrows(AppException.class, () -> subscriptionService.create(dto, mockActor));
    verify(subscriptionRepository, never()).save(any());
  }

  @Test
  @DisplayName("Delete Subscription - Sets status DELETED")
  void testDelete_Success() {
    when(subscriptionRepository.findById(subscriptionId)).thenReturn(Optional.of(mockSubscription));
    when(subscriptionRepository.save(mockSubscription)).thenReturn(mockSubscription);

    subscriptionService.delete(subscriptionId, mockActor);

    assertEquals(SubscriptionStatus.DELETED, mockSubscription.getStatus());
    verify(subscriptionRepository, times(1)).save(mockSubscription);
  }
}
