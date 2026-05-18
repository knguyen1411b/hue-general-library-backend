package org.app.backend.modules.librarycardrequest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;
import org.app.backend.modules.librarycardrequest.dto.LibraryCardRequestDTO;
import org.app.backend.modules.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class LibraryCardRequestServiceImplTest {

    @Mock
    private LibraryCardRequestRepository requestRepository;

    @InjectMocks
    private LibraryCardRequestServiceImpl libraryCardRequestService;

    private LibraryCardRequest mockRequest;
    private User mockUser;

    @BeforeEach
    void setUp() {
        mockUser =
                User.builder().id(java.util.UUID.randomUUID()).username("testuser").fullName("Test User").build();
        mockRequest =
                LibraryCardRequest.builder()
                        .user(mockUser)
                        .status(LibraryCardRequestStatus.PENDING)
                        .deliveryAddress("123 Main St")
                        .build();
    }

    @Test
    @DisplayName("Create Request - Success")
    void testCreateRequest_Success() {
        when(requestRepository.save(mockRequest)).thenReturn(mockRequest);

        LibraryCardRequest result = libraryCardRequestService.createRequest(mockRequest);

        assertNotNull(result);
        assertEquals(LibraryCardRequestStatus.PENDING, result.getStatus());
        verify(requestRepository, times(1)).save(mockRequest);
    }

    @Test
    @DisplayName("Get All Requests - Returns paginated DTO list")
    void testGetAllRequests_Success() {
        PageRequest pageable = PageRequest.of(0, 20);
        Page<LibraryCardRequest> page = new PageImpl<>(List.of(mockRequest), pageable, 1);
        when(requestRepository.findAll(pageable)).thenReturn(page);

        Page<LibraryCardRequestDTO> result = libraryCardRequestService.getAllRequests(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(requestRepository, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("Get Request By Id - Success")
    void testGetRequestById_Success() {
        when(requestRepository.findWithUserById(any()))
                .thenReturn(Optional.of(mockRequest));

        LibraryCardRequestDTO result = libraryCardRequestService.getRequestById(java.util.UUID.randomUUID());

        assertNotNull(result);
        assertEquals("123 Main St", result.getDeliveryAddress());
    }
}
