package org.app.backend.modules.notification;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.app.backend.modules.notification.enums.NotificationReadStatus;
import org.app.backend.modules.notification.enums.NotificationType;
import org.app.backend.modules.rental.Rental;
import org.app.backend.modules.rental.RentalRepository;
import org.app.backend.modules.rental.enums.RentalStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationScheduler {

  NotificationRepository notificationRepository;
  RentalRepository rentalRepository;
  NotificationService notificationService;

  /**
   * Cron chạy mỗi ngày lúc 00:00 - Kiểm tra những người sắp đến hạn mượn sách (dueDate trong 3 ngày tới)
   * Gửi thông báo nhắc nhở
   */
  @Scheduled(cron = "0 0 0 * * ?")
  @Transactional
  public void sendDueDateReminders() {
    log.info("[SCHEDULER] Bắt đầu gửi thông báo nhắc hạn mượn sách...");

    LocalDate today = LocalDate.now();
    LocalDate threeDaysLater = today.plusDays(3);

    // Tìm tất cả rental đang BORROWING có dueDate trong vòng 3 ngày tới
    List<Rental> upcomingDueRentals =
        rentalRepository.findBorrowingRentalsWithDueDateBetween(
            today, threeDaysLater);

    int sentCount = 0;
    for (Rental rental : upcomingDueRentals) {
      // Kiểm tra xem đã gửi thông báo nhắc hạn hôm nay chưa (tránh gửi trùng)
      boolean alreadySentToday =
          notificationRepository
              .findByUserIdAndTypeOrderByCreatedAtDesc(rental.getUserId(), NotificationType.RENTAL_DUE_REMINDER)
              .stream()
              .anyMatch(
                  n ->
                      n.getRelatedEntityId() != null
                          && n.getRelatedEntityId().equals(rental.getId())
                          && n.getCreatedAt().atZone(ZoneId.systemDefault()).toLocalDate().isEqual(today));

      if (!alreadySentToday) {
        // Tạo thông báo nhắc hạn
        notificationService.createReminderNotification(
            rental.getUserId(),
            NotificationType.RENTAL_DUE_REMINDER,
            "Nhắc nhở: Sách sắp đến hạn trả",
            "Bạn có phiếu mượn sách sẽ đến hạn trả vào ngày " + rental.getDueDate() + ". Vui lòng chuẩn bị trả sách đúng hạn.",
            rental.getId(),
            "RENTAL");
        sentCount++;
      }
    }

    log.info("[SCHEDULER] Đã gửi {} thông báo nhắc hạn mượn sách.", sentCount);
  }

  /**
   * Cron chạy mỗi 7 ngày (thứ 2) - Gửi thông báo quá hạn cho những người trễ hạn
   * Cấu hình: Chạy vào 9:00 sáng thứ 2 hàng tuần
   */
  @Scheduled(cron = "0 0 9 * * MON")
  @Transactional
  public void sendOverdueReminders() {
    log.info("[SCHEDULER] Bắt đầu gửi thông báo quá hạn mượn sách...");

    // Tìm tất cả rental đã quá hạn (OVERDUE)
    List<Rental> overdueRentals = rentalRepository.findByStatus(RentalStatus.OVERDUE);

    int sentCount = 0;
    for (Rental rental : overdueRentals) {
      // Kiểm tra xem đã gửi thông báo trong tuần này chưa
      // (Có thể mở rộng logic để tránh spam)
      notificationService.createReminderNotification(
          rental.getUserId(),
          NotificationType.OVERDUE_FINE,
          "Thông báo trễ hạn",
          "Bạn đang trễ hạn phiếu mượn sách. Vui lòng trả sách và thanh toán phí phạt để tránh bị khóa tài khoản.",
          rental.getId(),
          "RENTAL");
      sentCount++;
    }

    log.info("[SCHEDULER] Đã gửi {} thông báo quá hạn.", sentCount);
  }

  /**
   * Cron chạy mỗi ngày - Xóa các thông báo nhắc hạn khi người dùng đã trả sách
   */
  @Scheduled(cron = "0 0 0 * * ?")
  @Transactional
  public void clearResolvedNotifications() {
    log.info("[SCHEDULER] Bắt đầu dọn thông báo đã giải quyết...");

    int deletedCount = 0;

    // Tìm tất cả thông báo chưa đọc thuộc các loại: RENTAL_DUE_REMINDER, OVERDUE_FINE
    List<Notification> pendingReminders =
        notificationRepository.findByReadStatusAndTypeIn(
            NotificationReadStatus.UNREAD,
            List.of(NotificationType.RENTAL_DUE_REMINDER, NotificationType.OVERDUE_FINE));

    for (Notification notification : pendingReminders) {
      // Chỉ xử lý thông báo liên quan đến rental
      if (notification.getRelatedEntityId() != null
          && "RENTAL".equals(notification.getRelatedEntityType())) {
        Rental rental =
            rentalRepository
                .findById(notification.getRelatedEntityId())
                .orElse(null);

        // Nếu rental không tồn tại hoặc đã trả (RETURNED) hoặc mất (LOST) -> xóa thông báo
        if (rental == null
            || rental.getStatus() == RentalStatus.RETURNED
            || rental.getStatus() == RentalStatus.LOST) {
          notificationRepository.delete(notification);
          deletedCount++;
        }
      }
    }

    log.info("[SCHEDULER] Đã xóa {} thông báo đã giải quyết.", deletedCount);
  }
}
