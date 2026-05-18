package org.app.backend.modules.librarycardrequest;

import java.util.UUID;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.app.backend.common.exception.AppException;
import org.app.backend.modules.audit.AuditLogService;
import org.app.backend.modules.audit.enums.AuditLogAction;
import org.app.backend.modules.audit.enums.AuditLogEntity;
import org.app.backend.modules.audit.enums.AuditLogStatus;
import org.app.backend.modules.librarycardrequest.dto.LibraryCardRequestDTO;
import org.app.backend.modules.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LibraryCardRequestServiceImpl implements LibraryCardRequestService {
    private static final Logger log = LoggerFactory.getLogger(LibraryCardRequestServiceImpl.class);

    LibraryCardRequestRepository requestRepository;
    AuditLogService auditLogService;

    @Override
    @Transactional
    public LibraryCardRequest createRequest(LibraryCardRequest request) {
        LibraryCardRequest saved = requestRepository.save(request);
        safeAuditLog(
                saved.getUser().getId(),
                saved.getUser().getUsername(),
                AuditLogAction.LIBRARY_CARD_REQUEST_CREATED,
                saved.getId().toString(),
                AuditLogStatus.SUCCESS,
                "Độc giả gửi yêu cầu cấp thẻ vật lý"
        );
        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LibraryCardRequestDTO> getAllRequests(Pageable pageable) {
        return requestRepository.findAll(pageable).map(this::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LibraryCardRequestDTO> getAllRequests(
            Pageable pageable, UUID userId, LibraryCardRequestStatus status) {
        if (userId != null && status != null) {
            return requestRepository.findAll(pageable).map(this::toDTO);
        }
        if (userId != null) {
            return requestRepository.findByUserId(userId, pageable).map(this::toDTO);
        }
        if (status != null) {
            return requestRepository.findByStatus(status, pageable).map(this::toDTO);
        }
        return requestRepository.findAll(pageable).map(this::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public LibraryCardRequestDTO getRequestById(UUID id) {
        LibraryCardRequest request = requestRepository
                .findWithUserById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Không tìm thấy yêu cầu cấp thẻ"));
        return toDTO(request);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LibraryCardRequestDTO> getRequestsByUserId(UUID userId, Pageable pageable) {
        return requestRepository.findByUserId(userId, pageable).map(this::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasPendingRequestByUserId(UUID userId) {
        return requestRepository.existsByUserIdAndStatus(userId, LibraryCardRequestStatus.PENDING);
    }

    @Override
    @Transactional
    public LibraryCardRequestDTO updateRequestStatus(UUID id, LibraryCardRequestStatus status, String note) {
        LibraryCardRequest request = requestRepository
                .findWithUserById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Không tìm thấy yêu cầu cấp thẻ"));

        if (request.getStatus() == LibraryCardRequestStatus.APPROVED
                || request.getStatus() == LibraryCardRequestStatus.REJECTED) {
            throw new AppException(
                    HttpStatus.BAD_REQUEST,
                    "Yêu cầu này đã được xử lý (trạng thái: " + request.getStatus() + "), không thể cập nhật lại");
        }

        String oldNote = request.getNote();
        if (note != null && !note.isBlank()) {
            request.setNote(note);
        }
        request.setStatus(status);
        LibraryCardRequest saved = requestRepository.save(request);

        AuditLogAction action = status == LibraryCardRequestStatus.APPROVED
                ? AuditLogAction.LIBRARY_CARD_REQUEST_APPROVED
                : AuditLogAction.LIBRARY_CARD_REQUEST_REJECTED;

        String auditMsg = status == LibraryCardRequestStatus.APPROVED
                ? "Thủ thư duyệt yêu cầu cấp thẻ"
                : "Thủ thư từ chối yêu cầu cấp thẻ";

        if (note != null && !note.isBlank()) {
            auditMsg += " - Ghi chú: " + note;
        }

        safeAuditLog(
                saved.getUser().getId(),
                saved.getUser().getUsername(),
                action,
                saved.getId().toString(),
                AuditLogStatus.SUCCESS,
                auditMsg
        );

        return toDTO(saved);
    }

    @Override
    @Transactional
    public LibraryCardRequestDTO cancelRequest(UUID id, UUID userId) {
        LibraryCardRequest request = requestRepository
                .findWithUserById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Không tìm thấy yêu cầu cấp thẻ"));

        if (!request.getUser().getId().equals(userId)) {
            throw new AppException(HttpStatus.FORBIDDEN, "Bạn không có quyền hủy yêu cầu này");
        }

        if (request.getStatus() != LibraryCardRequestStatus.PENDING) {
            throw new AppException(
                    HttpStatus.BAD_REQUEST,
                    "Chỉ có thể hủy yêu cầu đang ở trạng thái chờ xử lý");
        }

        request.setStatus(LibraryCardRequestStatus.REJECTED);
        LibraryCardRequest saved = requestRepository.save(request);

        safeAuditLog(
                userId,
                saved.getUser().getUsername(),
                AuditLogAction.LIBRARY_CARD_REQUEST_CANCELED,
                saved.getId().toString(),
                AuditLogStatus.SUCCESS,
                "Độc giả hủy yêu cầu cấp thẻ"
        );

        return toDTO(saved);
    }

    private void safeAuditLog(UUID userId, String username, AuditLogAction action, String entityId, AuditLogStatus status, String message) {
        try {
            auditLogService.log(userId, username, action, AuditLogEntity.LIBRARY_CARD_REQUEST, entityId, status, message);
        } catch (Exception ex) {
            log.warn("Failed to write audit log: action={}, entityId={}", action, entityId, ex);
        }
    }

    private LibraryCardRequestDTO toDTO(LibraryCardRequest request) {
        User user = request.getUser();

        return LibraryCardRequestDTO.builder()
                .id(request.getId())
                .userId(user != null ? user.getId() : null)
                .username(user != null ? user.getUsername() : null)
                .fullName(user != null ? user.getFullName() : null)
                .email(user != null ? user.getEmail() : null)
                .phone(user != null ? user.getPhone() : null)
                .avatarUrl(user != null ? user.getAvatarUrl() : null)
                .deliveryAddress(request.getDeliveryAddress())
                .note(request.getNote())
                .status(request.getStatus().name())
                .createdAt(request.getCreatedAt())
                .updatedAt(request.getUpdatedAt())
                .build();
    }
}
