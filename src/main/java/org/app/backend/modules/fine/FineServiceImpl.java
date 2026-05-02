package org.app.backend.modules.fine;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.app.backend.common.exception.AppException;
import org.app.backend.modules.audit.AuditLogService;
import org.app.backend.modules.audit.enums.AuditLogAction;
import org.app.backend.modules.audit.enums.AuditLogEntity;
import org.app.backend.modules.audit.enums.AuditLogStatus;
import org.app.backend.modules.auth.security.CustomUserDetails;
import org.app.backend.modules.fine.dto.FineResponseDTO;
import org.app.backend.modules.fine.enums.FineStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FineServiceImpl implements FineService {

  FineRepository fineRepository;
  AuditLogService auditLogService;

  // Helper map Entity -> DTO thủ công (Hoặc bạn có thể dùng ModelMapper/MapStruct)
  private FineResponseDTO mapToDTO(Fine fine) {
    FineResponseDTO dto = new FineResponseDTO();
    dto.setId(fine.getId());
    dto.setAmount(fine.getAmount());
    dto.setReason(fine.getReason());
    dto.setStatus(fine.getStatus());
    dto.setRentalId(fine.getRental().getId());
    dto.setBarcode(fine.getRental().getBookItem().getBarcode());
    dto.setBookTitle(fine.getRental().getBookItem().getBook().getTitle());
    dto.setReaderName(fine.getRental().getUser().getFullName());
    return dto;
  }

  @Override
  public List<FineResponseDTO> getFinesByUserId(UUID userId, FineStatus status) {
    return fineRepository.findByRental_User_IdAndStatusOrderByAmountDesc(userId, status).stream()
        .map(this::mapToDTO)
        .collect(Collectors.toList());
  }

  @Override
  public Page<FineResponseDTO> getAllFines(FineStatus status, Pageable pageable) {
    return fineRepository.findByStatus(status, pageable).map(this::mapToDTO);
  }

  @Override
  @Transactional
  public void processPayment(UUID fineId, CustomUserDetails actor) {
    Fine fine =
        fineRepository
            .findById(fineId)
            .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Không tìm thấy phiếu phạt"));

    if (fine.getStatus() != FineStatus.UNPAID) {
      throw new AppException(
          HttpStatus.BAD_REQUEST, "Phiếu phạt này đã được thanh toán hoặc đã hủy");
    }

    // Cập nhật trạng thái thành Đã Thanh Toán
    fine.setStatus(FineStatus.PAID);
    fineRepository.save(fine);

    // Ghi Audit Log
    auditLogService.log(
        actor != null ? actor.getId() : null,
        actor != null ? actor.getUsername() : "system",
        AuditLogAction.UPDATE,
        AuditLogEntity.FINE,
        fine.getId().toString(),
        AuditLogStatus.SUCCESS,
        "Đã thu tiền nộp phạt: " + fine.getAmount() + " VNĐ");
  }

  @Override
  @Transactional
  public void cancelFine(UUID fineId, String reason, CustomUserDetails actor) {
    Fine fine =
        fineRepository
            .findById(fineId)
            .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Không tìm thấy phiếu phạt"));

    if (fine.getStatus() == FineStatus.PAID) {
      throw new AppException(HttpStatus.BAD_REQUEST, "Không thể hủy phiếu phạt đã thanh toán");
    }

    fine.setStatus(FineStatus.CANCELED);
    fine.setReason(fine.getReason() + " | LÝ DO HỦY: " + reason);
    fineRepository.save(fine);

    auditLogService.log(
        actor != null ? actor.getId() : null,
        actor != null ? actor.getUsername() : "system",
        AuditLogAction.UPDATE,
        AuditLogEntity.FINE,
        fine.getId().toString(),
        AuditLogStatus.SUCCESS,
        "Hủy phiếu phạt do sai sót. Lý do: " + reason);
  }
}
