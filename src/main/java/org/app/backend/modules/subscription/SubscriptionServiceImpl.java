package org.app.backend.modules.subscription;

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
  public void update(UUID id, SubscriptionUpdateDTO dto, CustomUserDetails actor) {
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
    if (dto.getMaxBooks() != null) subscription.setMaxBooks(dto.getMaxBooks());
    if (dto.getPrice() != null) subscription.setPrice(dto.getPrice());
    if (dto.getDurationDays() != null) subscription.setDurationDays(dto.getDurationDays());
    if (dto.getOverdueFeePerDay() != null)
      subscription.setOverdueFeePerDay(dto.getOverdueFeePerDay());
    if (dto.getMaxRenewals() != null) subscription.setMaxRenewals(dto.getMaxRenewals());
    if (dto.getCompensationRate() != null)
      subscription.setCompensationRate(dto.getCompensationRate());

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
    Subscription subscription =
        subscriptionRepository
            .findById(id)
            .orElseThrow(
                () ->
                    new AppException(
                        HttpStatus.NOT_FOUND, SubscriptionMessage.NOT_FOUND.getMessage()));

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
}
