package org.app.backend.modules.librarycardrequest.dto;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LibraryCardRequestDTO {
    private UUID id;
    private UUID userId;
    private String username;
    private String fullName;
    private String email;
    private String phone;
    private String avatarUrl;
    private String deliveryAddress;
    private String note;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
