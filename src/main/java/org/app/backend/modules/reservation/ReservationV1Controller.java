package org.app.backend.modules.reservation;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.app.backend.modules.bookItem.BookItem;
import org.app.backend.modules.bookItem.BookItemRepository;
import org.app.backend.modules.bookItem.BookItemStatus;
import org.app.backend.modules.reservation.dto.ReservationCreateDTO;
import org.app.backend.modules.reservation.dto.ReservationResponseDTO;
import org.app.backend.modules.user.User;
import org.app.backend.modules.user.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reservations")
@Validated
public class ReservationV1Controller {
  private static final Logger logger = LoggerFactory.getLogger(ReservationV1Controller.class);
  private final ReservationService reservationService;
  private final UserRepository userRepository;
  private final BookItemRepository bookItemRepository;

  public ReservationV1Controller(
      ReservationService reservationService,
      UserRepository userRepository,
      BookItemRepository bookItemRepository) {
    this.reservationService = reservationService;
    this.userRepository = userRepository;
    this.bookItemRepository = bookItemRepository;
  }

  @PostMapping
  public ResponseEntity<ReservationResponseDTO> createReservation(
      @Valid @RequestBody ReservationCreateDTO dto) {
    logger.info(
        "Creating reservation for user: {}, book item: {}", dto.getUserId(), dto.getBookItemId());
    User user =
        userRepository
            .findById(dto.getUserId())
            .orElseThrow(() -> new RuntimeException("User not found"));
    BookItem bookItem =
        bookItemRepository
            .findById(dto.getBookItemId())
            .orElseThrow(() -> new RuntimeException("Book item not found"));
    if (bookItem.getStatus() != BookItemStatus.AVAILABLE) {
      throw new org.app.backend.modules.reservation.exception.BookNotAvailableException(
          ReservationMessage.BOOK_NOT_AVAILABLE);
    }
    Reservation reservation = new Reservation();
    reservation.setUser(user);
    reservation.setBookItem(bookItem);
    Reservation created = reservationService.createReservation(reservation);
    return ResponseEntity.status(HttpStatus.CREATED).body(mapToResponseDTO(created));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ReservationResponseDTO> getReservationById(@PathVariable UUID id) {
    logger.debug("Getting reservation with id: {}", id);
    Reservation reservation = reservationService.getReservationById(id);
    return ResponseEntity.ok(mapToResponseDTO(reservation));
  }

  @PutMapping("/{id}/confirm")
  public ResponseEntity<ReservationResponseDTO> confirmReservation(@PathVariable UUID id) {
    logger.info("Confirming reservation with id: {}", id);
    Reservation confirmed = reservationService.confirmReservation(id);
    return ResponseEntity.ok(mapToResponseDTO(confirmed));
  }

  @PutMapping("/{id}/cancel")
  public ResponseEntity<ReservationResponseDTO> cancelReservation(@PathVariable UUID id) {
    logger.info("Canceling reservation with id: {}", id);
    Reservation canceled = reservationService.cancelReservation(id);
    return ResponseEntity.ok(mapToResponseDTO(canceled));
  }

  @GetMapping
  public ResponseEntity<List<ReservationResponseDTO>> getAllReservations() {
    logger.debug("Getting all reservations");
    List<Reservation> reservations = reservationService.getAllReservations();
    List<ReservationResponseDTO> response =
        reservations.stream().map(this::mapToResponseDTO).collect(Collectors.toList());
    return ResponseEntity.ok(response);
  }

  private ReservationResponseDTO mapToResponseDTO(Reservation reservation) {
    return ReservationResponseDTO.builder()
        .id(reservation.getId())
        .userId(reservation.getUser().getId())
        .userName(reservation.getUser().getFullName())
        .bookItemId(reservation.getBookItem().getId())
        .bookTitle(reservation.getBookItem().getBook().getTitle())
        .status(reservation.getStatus())
        .reservationDate(reservation.getReservationDate())
        .confirmDate(reservation.getConfirmDate())
        .cancelDate(reservation.getCancelDate())
        .build();
  }
}
