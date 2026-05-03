package org.app.backend.modules.reservation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.app.backend.common.dto.DataApiResponse;
import org.app.backend.common.exception.AppException;
import org.app.backend.common.swagger.BadRequestApiResponse;
import org.app.backend.common.swagger.NotFoundApiResponse;
import org.app.backend.common.swagger.UnauthorizedApiResponse;
import org.app.backend.modules.bookItem.BookItem;
import org.app.backend.modules.bookItem.BookItemRepository;
import org.app.backend.modules.bookItem.enums.BookItemStatus;
import org.app.backend.modules.reservation.dto.ReservationCreateDTO;
import org.app.backend.modules.reservation.dto.ReservationResponseDTO;
import org.app.backend.modules.user.User;
import org.app.backend.modules.user.UserRepository;
import org.springframework.http.HttpStatus;
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

  @Operation(
      summary = "Tạo phiếu đặt trước sách",
      responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = DataApiResponseReservationDTO.class)))
      })
  @BadRequestApiResponse
  @NotFoundApiResponse
  @UnauthorizedApiResponse
  @PostMapping
  public DataApiResponse<ReservationResponseDTO> createReservation(
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
    return DataApiResponse.<ReservationResponseDTO>builder()
        .success(true)
        .statusCode(HttpStatus.CREATED.value())
        .message("Tạo phiếu đặt trước thành công")
        .data(mapToResponseDTO(created))
        .build();
  }

  @Operation(
      summary = "Lấy chi tiết phiếu đặt trước theo ID",
      responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = DataApiResponseReservationDTO.class)))
      })
  @NotFoundApiResponse
  @UnauthorizedApiResponse
  @GetMapping("/{id}")
  public DataApiResponse<ReservationResponseDTO> getReservationById(@PathVariable UUID id) {
    Reservation reservation = reservationService.getReservationById(id);
    return DataApiResponse.success(
        mapToResponseDTO(reservation), "Lấy chi tiết đặt trước thành công");
  }

  @Operation(
      summary = "Xác nhận phiếu đặt trước",
      responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = DataApiResponseReservationDTO.class)))
      })
  @NotFoundApiResponse
  @UnauthorizedApiResponse
  @PutMapping("/{id}/confirm")
  public DataApiResponse<ReservationResponseDTO> confirmReservation(@PathVariable UUID id) {
    Reservation confirmed = reservationService.confirmReservation(id);
    return DataApiResponse.success(mapToResponseDTO(confirmed), "Xác nhận đặt trước thành công");
  }

  @Operation(
      summary = "Hủy phiếu đặt trước",
      responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = DataApiResponseReservationDTO.class)))
      })
  @NotFoundApiResponse
  @UnauthorizedApiResponse
  @PutMapping("/{id}/cancel")
  public DataApiResponse<ReservationResponseDTO> cancelReservation(@PathVariable UUID id) {
    Reservation canceled = reservationService.cancelReservation(id);
    return DataApiResponse.success(mapToResponseDTO(canceled), "Hủy đặt trước thành công");
  }

  @Operation(
      summary = "Lấy danh sách phiếu đặt trước",
      responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = DataApiResponseReservationList.class)))
      })
  @UnauthorizedApiResponse
  @GetMapping
  public DataApiResponse<List<ReservationResponseDTO>> getAllReservations() {
    List<Reservation> reservations = reservationService.getAllReservations();
    List<ReservationResponseDTO> response =
        reservations.stream().map(this::mapToResponseDTO).collect(Collectors.toList());
    return DataApiResponse.success(response, "Lấy danh sách đặt trước thành công");
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

  public static class DataApiResponseReservationDTO
      extends DataApiResponse<ReservationResponseDTO> {}

  public static class DataApiResponseReservationList
      extends DataApiResponse<List<ReservationResponseDTO>> {}
}
