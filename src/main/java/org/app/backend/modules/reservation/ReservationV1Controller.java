package org.app.backend.modules.reservation;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.app.backend.common.exception.AppException;
import org.app.backend.modules.bookItem.BookItem;
import org.app.backend.modules.bookItem.BookItemRepository;
import org.app.backend.modules.bookItem.enums.BookItemStatus;
import org.app.backend.modules.reservation.dto.ReservationCreateDTO;
import org.app.backend.modules.reservation.dto.ReservationResponseDTO;
import org.app.backend.modules.user.User;
import org.app.backend.modules.user.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reservations")
@RequiredArgsConstructor
@Tag(name = "Đặt trước sách (V1)")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Validated
public class ReservationV1Controller {
  ReservationService reservationService;
  UserRepository userRepository;
  BookItemRepository bookItemRepository;

  @PostMapping
  public ResponseEntity<ReservationResponseDTO> createReservation(
      @Valid @RequestBody ReservationCreateDTO dto) {
    User user =
        userRepository
            .findById(dto.getUserId())
            .orElseThrow(
                () ->
                    new AppException(
                        HttpStatus.NOT_FOUND, ReservationMessage.USER_NOT_FOUND.getMessage()));
    BookItem bookItem =
        bookItemRepository
            .findById(dto.getBookItemId())
            .orElseThrow(
                () ->
                    new AppException(
                        HttpStatus.NOT_FOUND, ReservationMessage.BOOK_ITEM_NOT_FOUND.getMessage()));
    if (bookItem.getStatus() != BookItemStatus.AVAILABLE) {
      throw new AppException(
          HttpStatus.BAD_REQUEST, ReservationMessage.BOOK_NOT_AVAILABLE.getMessage());
    }
    Reservation reservation = new Reservation();
    reservation.setUser(user);
    reservation.setBookItem(bookItem);
    Reservation created = reservationService.createReservation(reservation);
    return ResponseEntity.status(HttpStatus.CREATED).body(mapToResponseDTO(created));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ReservationResponseDTO> getReservationById(@PathVariable UUID id) {
    Reservation reservation = reservationService.getReservationById(id);
    return ResponseEntity.ok(mapToResponseDTO(reservation));
  }

  @PutMapping("/{id}/confirm")
  public ResponseEntity<ReservationResponseDTO> confirmReservation(@PathVariable UUID id) {
    Reservation confirmed = reservationService.confirmReservation(id);
    return ResponseEntity.ok(mapToResponseDTO(confirmed));
  }

  @PutMapping("/{id}/cancel")
  public ResponseEntity<ReservationResponseDTO> cancelReservation(@PathVariable UUID id) {
    Reservation canceled = reservationService.cancelReservation(id);
    return ResponseEntity.ok(mapToResponseDTO(canceled));
  }

  @GetMapping
  public ResponseEntity<List<ReservationResponseDTO>> getAllReservations() {
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
