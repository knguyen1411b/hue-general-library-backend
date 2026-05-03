package org.app.backend.modules.reservation;

import java.util.List;
import java.util.UUID;
import org.app.backend.modules.bookItem.BookItem;
import org.app.backend.modules.bookItem.BookItemRepository;
import org.app.backend.modules.bookItem.enums.BookItemStatus;
import org.app.backend.modules.reservation.exception.ReservationNotFoundException;
import org.app.backend.modules.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ReservationServiceImpl implements ReservationService {
  private final ReservationRepository reservationRepository;
  private final UserRepository userRepository;
  private final BookItemRepository bookItemRepository;

  public ReservationServiceImpl(
      ReservationRepository reservationRepository,
      UserRepository userRepository,
      BookItemRepository bookItemRepository) {
    this.reservationRepository = reservationRepository;
    this.userRepository = userRepository;
    this.bookItemRepository = bookItemRepository;
  }

  @Override
  public Reservation createReservation(Reservation reservation) {
    return reservationRepository.save(reservation);
  }

  @Override
  public Reservation getReservationById(UUID id) {
    return reservationRepository
        .findById(id)
        .orElseThrow(
            () -> new ReservationNotFoundException(ReservationMessage.RESERVATION_NOT_FOUND));
  }

  @Override
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
  public List<Reservation> getAllReservations() {
    return reservationRepository.findAll();
  }
}
