package org.app.backend.modules.notification;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.app.backend.common.exception.AppException;
import org.app.backend.modules.auth.security.CustomUserDetails;
import org.app.backend.modules.notification.dto.*;
import org.app.backend.modules.notification.Notification;
import org.app.backend.modules.notification.enums.NotificationReadStatus;
import org.app.backend.modules.notification.enums.NotificationStatus;
import org.app.backend.modules.notification.enums.NotificationType;
import org.app.backend.modules.user.User;
import org.app.backend.modules.user.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationServiceImpl implements NotificationService {
  NotificationRepository notificationRepository;
  UserRepository userRepository;
  ModelMapper modelMapper;

  @Override
  @Transactional(readOnly = true)
  public Page<NotificationDTO> findAllMyNotifications(CustomUserDetails user, NotificationFilterDTO filter, Pageable pageable) {
    filter.setUserId(user.getId());
    Specification<Notification> spec = NotificationSpecification.filter(filter);
    return notificationRepository.findAll(spec, pageable)
        .map(noti -> modelMapper.map(noti, NotificationDTO.class));
  }

  @Override
  @Transactional(readOnly = true)
  public NotificationDTO findById(UUID id, CustomUserDetails user) {
    return notificationRepository.findByIdAndUser_Id(id, user.getId())
        .map(noti -> modelMapper.map(noti, NotificationDTO.class))
        .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Không tìm thấy thông báo"));
  }

  @Override
  @Transactional(readOnly = true)
  public long getUnreadCount(CustomUserDetails user) {
    return notificationRepository.countByUser_IdAndReadStatus(user.getId(), NotificationReadStatus.UNREAD);
  }

  @Override
  @Transactional(readOnly = true)
  public NotificationStatsDTO getStats(CustomUserDetails user) {
    long total = notificationRepository.countByUser_Id(user.getId());
    long unread = getUnreadCount(user);
    long read = total - unread;
    return NotificationStatsDTO.builder()
        .totalCount(total)
        .unreadCount(unread)
        .readCount(read)
        .build();
  }

  @Override
  @Transactional
  public void markAsRead(UUID id, CustomUserDetails user) {
    Notification noti = notificationRepository.findByIdAndUser_Id(id, user.getId())
        .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Không tìm thấy thông báo"));
    
    if (noti.getReadStatus() == NotificationReadStatus.UNREAD) {
      noti.setReadStatus(NotificationReadStatus.READ);
      noti.setReadAt(Instant.now());
      notificationRepository.save(noti);
    }
  }

  @Override
  @Transactional
  public void markAllAsRead(CustomUserDetails user) {
    List<Notification> unreadNotis = notificationRepository.findByUser_IdAndReadStatus(user.getId(), NotificationReadStatus.UNREAD);
    Instant now = Instant.now();
    unreadNotis.forEach(noti -> {
      noti.setReadStatus(NotificationReadStatus.READ);
      noti.setReadAt(now);
    });
    notificationRepository.saveAll(unreadNotis);
  }

  @Override
  @Transactional
  public void deleteNotification(UUID id, CustomUserDetails user) {
    Notification noti = notificationRepository.findByIdAndUser_Id(id, user.getId())
        .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Không tìm thấy thông báo"));
    notificationRepository.delete(noti);
  }

  @Override
  @Transactional
  public NotificationDTO createReminderNotification(UUID userId, NotificationType type, String title, String message, UUID relatedEntityId, String relatedEntityType) {
    User targetUser = userRepository.findById(userId)
        .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Không tìm thấy người dùng"));

    Notification noti = Notification.builder()
        .user(targetUser)
        .type(type)
        .title(title)
        .message(message)
        .relatedEntityId(relatedEntityId)
        .relatedEntityType(relatedEntityType)
        .readStatus(NotificationReadStatus.UNREAD)
        .notificationStatus(NotificationStatus.PENDING)
        .build();

    Notification saved = notificationRepository.save(noti);
    return modelMapper.map(saved, NotificationDTO.class);
  }
}
