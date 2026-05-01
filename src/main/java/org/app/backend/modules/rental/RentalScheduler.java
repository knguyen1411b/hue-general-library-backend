package org.app.backend.modules.rental;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.app.backend.modules.fine.Fine;
import org.app.backend.modules.fine.FineRepository;
import org.app.backend.modules.fine.enums.FineStatus;
import org.app.backend.modules.rental.enums.RentalStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RentalScheduler {

  RentalRepository rentalRepository;
  FineRepository fineRepository;

  /**
   * Cron: "0 0 0 * * ?" -> Chạy vào lúc 00:00:00 (nửa đêm) mỗi ngày. Cú pháp Cron Spring: Giây -
   * Phút - Giờ - Ngày - Tháng - Thứ
   */
  @Scheduled(cron = "0 0 0 * * ?")
  @Transactional
  public void autoCheckOverdueRentals() {
    log.info("[CRON JOB] Bắt đầu quét các phiếu mượn sách quá hạn...");

    // Tìm tất cả các phiếu đang mượn (BORROWING) nhưng hạn trả (dueDate) đã qua (nhỏ hơn Hiện tại)
    List<Rental> overdueRentals =
        rentalRepository.findByStatusAndDueDateBefore(RentalStatus.BORROWING, Instant.now());

    if (overdueRentals.isEmpty()) {
      log.info("[CRON JOB] Không có phiếu mượn nào quá hạn hôm nay.");
      return;
    }

    List<Rental> updatedRentals = new ArrayList<>();
    List<Fine> newFines = new ArrayList<>();

    for (Rental rental : overdueRentals) {
      // Đổi cờ thành quá hạn
      rental.setStatus(RentalStatus.OVERDUE);
      updatedRentals.add(rental);

      // Sinh một phiếu phạt khởi điểm 5000đ để khóa tài khoản (chặn mượn tiếp)
      // (Khi nào họ mang sách tới trả thực tế, hàm returnBooks sẽ update lại số tiền chuẩn dựa trên
      // tổng số ngày trễ)
      Fine fine =
          Fine.builder()
              .rental(rental)
              .amount(5000)
              .reason("Phạt trễ hạn sách: " + rental.getBookItem().getBook().getTitle())
              .status(FineStatus.UNPAID)
              .build();
      newFines.add(fine);
    }

    rentalRepository.saveAll(updatedRentals);
    fineRepository.saveAll(newFines);

    log.info(
        "[CRON JOB] Đã cập nhật {} phiếu mượn thành OVERDUE và tạo {} phiếu phạt.",
        updatedRentals.size(),
        newFines.size());
  }
}
