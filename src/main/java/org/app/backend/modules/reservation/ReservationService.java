package org.app.backend.modules.reservation;

import java.util.List;
import java.util.UUID;

public interface ReservationService {
  Reservation createReservation(Reservation reservation);

  Reservation getReservationById(UUID id);

  Reservation confirmReservation(UUID id);

  Reservation cancelReservation(UUID id);

  List<Reservation> getAllReservations();
}
