package org.app.backend.modules.notification;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.app.backend.modules.notification.enums.NotificationReadStatus;
import org.app.backend.modules.notification.enums.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NotificationRepository extends JpaRepository<Notification, UUID>, JpaSpecificationExecutor<Notification> {
  List<Notification> findByUser_Id(UUID userId);

  Page<Notification> findByUser_Id(UUID userId, Pageable pageable);

  long countByUser_IdAndReadStatus(UUID userId, NotificationReadStatus readStatus);

  long countByUser_Id(UUID userId);

  List<Notification> findByUser_IdAndReadStatus(UUID userId, NotificationReadStatus readStatus);

  @Query("SELECT n FROM Notification n WHERE n.user.id = :userId AND n.type = :type AND n.readStatus = :readStatus ORDER BY n.createdAt DESC")
  List<Notification> findUnreadByType(@Param("userId") UUID userId, @Param("type") NotificationType type, @Param("readStatus") NotificationReadStatus readStatus);

  @Query("SELECT n FROM Notification n WHERE n.user.id = :userId AND n.type = :type ORDER BY n.createdAt DESC")
  List<Notification> findByUserIdAndTypeOrderByCreatedAtDesc(@Param("userId") UUID userId, @Param("type") NotificationType type);

  List<Notification> findByReadStatusAndTypeIn(NotificationReadStatus readStatus, List<NotificationType> types);

  @Query("SELECT n FROM Notification n WHERE n.id = :id AND n.user.id = :userId")
  Optional<Notification> findByIdAndUser_Id(@Param("id") UUID id, @Param("userId") UUID userId);
}
