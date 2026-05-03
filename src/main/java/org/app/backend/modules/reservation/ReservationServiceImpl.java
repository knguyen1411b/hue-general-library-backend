package org.app.backend.modules.reservation;

import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.app.backend.common.exception.AppException;
import org.app.backend.modules.bookItem.BookItem;
import org.app.backend.modules.bookItem.BookItemRepository;
import org.app.backend.modules.bookItem.enums.BookItemStatus;
import org.app.backend.modules.user.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReservationServiceImpl implements ReservationService {
  ReservationRepository reservationRepository;
  UserRepository userRepository;
  BookItemRepository bookItemRepository;

  @Override
  @Transactional
  public Reservation createReservation(Reservation reservation) {
    return reservationRepository.save(reservation);
  }

  @Override
  @Transactional(readOnly = true)
  public Reservation getReservationById(UUID id) {
    return reservationRepository
        .findById(id)
        .orElseThrow(
            () ->
                new AppException(
                    HttpStatus.NOT_FOUND, ReservationMessage.RESERVATION_NOT_FOUND.getMessage()));
  }

  @Override
  @Transactional
  public Reservation confirmReservation(UUID id) {
    Reservation existing = getReservationById(id);
    existing.setStatus(ReservationStatus.CONFIRMED);
    // Update book item status to PENDING if available
    BookItem bookItem = existing.getBookItem();
    if (bookItem.getStatus() == BookItemStatus.AVAILABLE) {
      bookItem.setStatus(BookItemStatus.PENDING);
      bookItemRepository.save(bookItem);
    }
    return reservationRepository.save(existing);
  }

  @Override
  @Transactional
  public Reservation cancelReservation(UUID id) {
    Reservation existing = getReservationById(id);
    existing.setStatus(ReservationStatus.CANCELLED);
    // Revert book item status to AVAILABLE if it was PENDING
    BookItem bookItem = existing.getBookItem();
    if (bookItem.getStatus() == BookItemStatus.PENDING) {
      bookItem.setStatus(BookItemStatus.AVAILABLE);
      bookItemRepository.save(bookItem);
    }
    return reservationRepository.save(existing);
  }

  @Override
  @Transactional(readOnly = true)
  public List<Reservation> getAllReservations() {
    return reservationRepository.findAll();
  }
}
