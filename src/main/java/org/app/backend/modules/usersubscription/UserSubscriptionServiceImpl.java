package org.app.backend.modules.usersubscription;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.app.backend.common.exception.AppException;
import org.app.backend.modules.subscription.Subscription;
import org.app.backend.modules.subscription.SubscriptionRepository;
import org.app.backend.modules.user.User;
import org.app.backend.modules.user.UserRepository;
import org.app.backend.modules.usersubscription.dto.UserSubscriptionCreateDTO;
import org.app.backend.modules.usersubscription.dto.UserSubscriptionResponseDTO;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserSubscriptionServiceImpl implements UserSubscriptionService {

  UserSubscriptionRepository userSubscriptionRepository;
  SubscriptionRepository subscriptionRepository;
  UserRepository userRepository;
  ModelMapper modelMapper;

  @Override
  @Transactional
  public UserSubscriptionResponseDTO create(UserSubscriptionCreateDTO dto) {
    User managedUser =
        userRepository
            .findById(dto.getUserId())
            .orElseThrow(
                () ->
                    new AppException(
                        HttpStatus.BAD_REQUEST, UserSubscriptionMessage.USER_NOT_FOUND.getMessage()));

    Subscription managedSubscription =
        subscriptionRepository
            .findById(dto.getSubscriptionId())
            .orElseThrow(
                () ->
                    new AppException(
                        HttpStatus.BAD_REQUEST,
                        UserSubscriptionMessage.SUBSCRIPTION_NOT_FOUND.getMessage()));

    if (userSubscriptionRepository.existsByUserIdAndStatus(
        managedUser.getId(), UserSubscriptionStatus.ACTIVE)) {
      throw new AppException(
          HttpStatus.BAD_REQUEST, UserSubscriptionMessage.USER_ALREADY_HAS_ACTIVE.getMessage());
    }

    LocalDate startDate = LocalDate.now();
    LocalDate endDate = startDate.plusDays(managedSubscription.getDurationDays());

    UserSubscription entity =
        UserSubscription.builder()
            .user(managedUser)
            .subscription(managedSubscription)
            .startDate(startDate)
            .endDate(endDate)
            .status(UserSubscriptionStatus.ACTIVE)
            .maxBooks(managedSubscription.getMaxBooks())
            .price(managedSubscription.getPrice())
            .build();

    UserSubscription saved = userSubscriptionRepository.save(entity);
    return modelMapper.map(saved, UserSubscriptionResponseDTO.class);
  }

  @Override
  @Transactional(readOnly = true)
  public UserSubscriptionResponseDTO getById(UUID id) {
    UserSubscription entity = getEntity(id);
    return modelMapper.map(entity, UserSubscriptionResponseDTO.class);
  }

  @Override
  @Transactional(readOnly = true)
  public List<UserSubscriptionResponseDTO> getAll() {
    return userSubscriptionRepository.findAll().stream()
        .map(e -> modelMapper.map(e, UserSubscriptionResponseDTO.class))
        .toList();
  }

  @Override
  @Transactional
  public UserSubscriptionResponseDTO update(UUID id, UserSubscription userSubscription) {
    UserSubscription existing = getEntity(id);
    UserSubscription merged = merge(existing, userSubscription);
    validateUserSubscription(merged);
    UserSubscription saved = userSubscriptionRepository.save(merged);
    return modelMapper.map(saved, UserSubscriptionResponseDTO.class);
  }

  @Override
  @Transactional
  public void delete(UUID id) {
    getEntity(id);
    userSubscriptionRepository.deleteById(id);
  }

  private UserSubscription getEntity(UUID id) {
    return userSubscriptionRepository
        .findById(id)
        .orElseThrow(
            () ->
                new AppException(
                    HttpStatus.NOT_FOUND, UserSubscriptionMessage.NOT_FOUND.getMessage()));
  }

  private UserSubscription merge(UserSubscription existing, UserSubscription update) {
    if (update.getUser() != null) existing.setUser(update.getUser());
    if (update.getSubscription() != null) existing.setSubscription(update.getSubscription());
    if (update.getStartDate() != null) existing.setStartDate(update.getStartDate());
    if (update.getEndDate() != null) existing.setEndDate(update.getEndDate());
    if (update.getStatus() != null) existing.setStatus(update.getStatus());
    if (update.getMaxBooks() != null) existing.setMaxBooks(update.getMaxBooks());
    if (update.getPrice() != null) existing.setPrice(update.getPrice());
    return existing;
  }

  private void validateUserSubscription(UserSubscription userSubscription) {
    if (userSubscription.getUser() == null) {
      throw new AppException(
          HttpStatus.BAD_REQUEST, UserSubscriptionMessage.VALIDATION_USER_REQUIRED.getMessage());
    }
    if (userSubscription.getSubscription() == null) {
      throw new AppException(
          HttpStatus.BAD_REQUEST,
          UserSubscriptionMessage.VALIDATION_SUBSCRIPTION_REQUIRED.getMessage());
    }
    if (userSubscription.getStartDate() == null) {
      throw new AppException(
          HttpStatus.BAD_REQUEST,
          UserSubscriptionMessage.VALIDATION_START_DATE_REQUIRED.getMessage());
    }
    if (userSubscription.getEndDate() == null) {
      throw new AppException(
          HttpStatus.BAD_REQUEST,
          UserSubscriptionMessage.VALIDATION_END_DATE_REQUIRED.getMessage());
    }
    if (userSubscription.getEndDate().isBefore(userSubscription.getStartDate())) {
      throw new AppException(
          HttpStatus.BAD_REQUEST, UserSubscriptionMessage.END_DATE_BEFORE_START_DATE.getMessage());
    }
  }
}