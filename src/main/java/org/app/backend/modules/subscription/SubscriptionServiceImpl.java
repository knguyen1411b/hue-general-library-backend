package org.app.backend.modules.subscription;

import jakarta.validation.Valid;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.app.backend.common.exception.AppException;
import org.app.backend.modules.audit.AuditLogService;
import org.app.backend.modules.audit.enums.AuditLogAction;
import org.app.backend.modules.audit.enums.AuditLogEntity;
import org.app.backend.modules.audit.enums.AuditLogStatus;
import org.app.backend.modules.auth.security.CustomUserDetails;
import org.app.backend.modules.subscription.dto.SubscriptionCreateDTO;
import org.app.backend.modules.subscription.dto.SubscriptionDTO;
import org.app.backend.modules.subscription.dto.SubscriptionUpdateDTO;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SubscriptionServiceImpl implements SubscriptionService {

  SubscriptionRepository subscriptionRepository;
  ModelMapper modelMapper;
  AuditLogService auditLogService;

  @Override
  @Transactional(readOnly = true)
  public Page<SubscriptionDTO> findAll(Pageable pageable) {
    return subscriptionRepository
        .findAll(pageable)
        .map(sub -> modelMapper.map(sub, SubscriptionDTO.class));
  }

  @Override
  @Transactional(readOnly = true)
  public SubscriptionDTO findById(UUID id) {
    return subscriptionRepository
        .findById(id)
        .map(sub -> modelMapper.map(sub, SubscriptionDTO.class))
        .orElseThrow(
            () ->
                new AppException(HttpStatus.NOT_FOUND, SubscriptionMessage.NOT_FOUND.getMessage()));
  }

  @Override
  @Transactional(readOnly = true)
  public SubscriptionDTO findByKey(String key) {
    return subscriptionRepository
        .findByKey(key)
        .map(sub -> modelMapper.map(sub, SubscriptionDTO.class))
        .orElseThrow(
            () ->
                new AppException(HttpStatus.NOT_FOUND, SubscriptionMessage.NOT_FOUND.getMessage()));
  }

  @Override
  @Transactional
  public void create(SubscriptionCreateDTO dto, CustomUserDetails actor) {
    validateActorPermission(actor, "CREATE");

    String normalizedKey = dto.getKey().toUpperCase();
    if (subscriptionRepository.existsByKey(normalizedKey)) {
      throw new AppException(HttpStatus.CONFLICT, SubscriptionMessage.KEY_EXISTS.getMessage());
    }
    if (subscriptionRepository.existsByName(dto.getName())) {
      throw new AppException(HttpStatus.CONFLICT, SubscriptionMessage.NAME_EXISTS.getMessage());
    }

    Subscription subscription = modelMapper.map(dto, Subscription.class);
    subscription.setKey(normalizedKey);
    subscriptionRepository.save(subscription);

    auditLogService.log(
        actor != null ? actor.getId() : null,
        actor != null ? actor.getUsername() : "system",
        AuditLogAction.CREATE,
        AuditLogEntity.SUBSCRIPTION,
        subscription.getId().toString(),
        AuditLogStatus.SUCCESS,
        SubscriptionMessage.CREATE_SUCCESS.getMessage());
  }

  @Override
  @Transactional
  public void update(UUID id, @Valid SubscriptionUpdateDTO dto, CustomUserDetails actor) {
    validateActorPermission(actor, "UPDATE");

    Subscription subscription =
        subscriptionRepository
            .findById(id)
            .orElseThrow(
                () ->
                    new AppException(
                        HttpStatus.NOT_FOUND, SubscriptionMessage.NOT_FOUND.getMessage()));

    if (dto.getKey() != null && !dto.getKey().equalsIgnoreCase(subscription.getKey())) {
      String normalizedKey = dto.getKey().toUpperCase();
      if (subscriptionRepository.existsByKey(normalizedKey)) {
        throw new AppException(HttpStatus.CONFLICT, SubscriptionMessage.KEY_EXISTS.getMessage());
      }
      subscription.setKey(normalizedKey);
    }
    if (dto.getName() != null && !dto.getName().equals(subscription.getName())) {
      if (subscriptionRepository.existsByName(dto.getName())) {
        throw new AppException(HttpStatus.CONFLICT, SubscriptionMessage.NAME_EXISTS.getMessage());
      }
      subscription.setName(dto.getName());
    }
    if (dto.getMaxBooks() != null) {
      validatePositive(dto.getMaxBooks(), "maxBooks");
      subscription.setMaxBooks(dto.getMaxBooks());
    }
    if (dto.getPrice() != null) {
      validateNonNegative(dto.getPrice(), "price");
      subscription.setPrice(dto.getPrice());
    }
    if (dto.getDurationDays() != null) {
      validatePositive(dto.getDurationDays(), "durationDays");
      subscription.setDurationDays(dto.getDurationDays());
    }
    if (dto.getOverdueFeePerDay() != null) {
      validateNonNegative(dto.getOverdueFeePerDay(), "overdueFeePerDay");
      subscription.setOverdueFeePerDay(dto.getOverdueFeePerDay());
    }
    if (dto.getMaxRenewals() != null) {
      validateNonNegative(dto.getMaxRenewals(), "maxRenewals");
      subscription.setMaxRenewals(dto.getMaxRenewals());
    }
    if (dto.getCompensationRate() != null) {
      validateNonNegative(dto.getCompensationRate(), "compensationRate");
      subscription.setCompensationRate(dto.getCompensationRate());
    }

    subscriptionRepository.save(subscription);

    auditLogService.log(
        actor != null ? actor.getId() : null,
        actor != null ? actor.getUsername() : "system",
        AuditLogAction.UPDATE,
        AuditLogEntity.SUBSCRIPTION,
        subscription.getId().toString(),
        AuditLogStatus.SUCCESS,
        SubscriptionMessage.UPDATE_SUCCESS.getMessage());
  }

  @Override
  @Transactional
  public void delete(UUID id, CustomUserDetails actor) {
    validateActorPermission(actor, "DELETE");

    Subscription subscription =
        subscriptionRepository
            .findById(id)
            .orElseThrow(
                () ->
                    new AppException(
                        HttpStatus.NOT_FOUND, SubscriptionMessage.NOT_FOUND.getMessage()));

    // Check if subscription is being used by any active user subscription
    // ---
    // ///
    //
    //
    subscription.setStatus(SubscriptionStatus.DELETED);
    subscriptionRepository.save(subscription);

    auditLogService.log(
        actor != null ? actor.getId() : null,
        actor != null ? actor.getUsername() : "system",
        AuditLogAction.DELETE,
        AuditLogEntity.SUBSCRIPTION,
        subscription.getId().toString(),
        AuditLogStatus.SUCCESS,
        SubscriptionMessage.DELETE_SUCCESS.getMessage());
  }

  private void validateActorPermission(CustomUserDetails actor, String action) {
    if (actor == null) {
      throw new AppException(
          HttpStatus.UNAUTHORIZED, "Yêu cầu xác thực để thực hiện thao tác " + action);
    }
    // TODO: Implement actual role/permission check
    // For now, allow all authenticated users
  }

  private void validatePositive(Integer value, String fieldName) {
    if (value <= 0) {
      throw new AppException(HttpStatus.BAD_REQUEST, fieldName + " phải lớn hơn 0");
    }
  }

  private void validateNonNegative(Integer value, String fieldName) {
    if (value < 0) {
      throw new AppException(HttpStatus.BAD_REQUEST, fieldName + " phải lớn hơn hoặc bằng 0");
    }
  }
}
