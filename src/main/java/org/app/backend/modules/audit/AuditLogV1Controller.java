package org.app.backend.modules.audit;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.app.backend.common.dto.DataApiResponse;
import org.app.backend.common.dto.PagedApiResponse;
import org.app.backend.common.swagger.ForbiddenApiResponse;
import org.app.backend.common.swagger.NotFoundApiResponse;
import org.app.backend.common.swagger.UnauthorizedApiResponse;
import org.app.backend.modules.audit.dto.AuditLogDTO;
import org.app.backend.modules.audit.dto.AuditLogFilterDTO;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/audit-logs")
@RequiredArgsConstructor
@Tag(name = "Nhật ký hệ thống (V1)")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuditLogV1Controller {
  AuditLogService auditLogService;

  @Operation(
      summary = "Lấy danh sách nhật ký hệ thống",
      responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = PagedApiResponseAuditLogDTO.class)))
      })
  @UnauthorizedApiResponse
  @ForbiddenApiResponse
  @GetMapping
  @PreAuthorize("hasRole('ADMIN')")
  public PagedApiResponse<AuditLogDTO> index(
      @ParameterObject AuditLogFilterDTO filter, @ParameterObject Pageable pageable) {
    return PagedApiResponse.success(
        auditLogService.findAll(filter, pageable), AuditLogMessage.INDEX_SUCCESS.getMessage());
  }

  @Operation(
      summary = "Lấy chi tiết nhật ký hệ thống",
      parameters = {@Parameter(name = "id", required = true)},
      responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = DataApiResponseAuditLogDTO.class)))
      })
  @UnauthorizedApiResponse
  @ForbiddenApiResponse
  @NotFoundApiResponse
  @GetMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public DataApiResponse<AuditLogDTO> show(@PathVariable UUID id) {
    return DataApiResponse.success(
        auditLogService.findById(id), AuditLogMessage.SHOW_SUCCESS.getMessage());
  }

  public static class PagedApiResponseAuditLogDTO extends PagedApiResponse<AuditLogDTO> {}

  public static class DataApiResponseAuditLogDTO extends DataApiResponse<AuditLogDTO> {}
}
