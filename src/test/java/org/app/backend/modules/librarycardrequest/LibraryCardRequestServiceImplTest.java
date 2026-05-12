package org.app.backend.modules.librarycardrequest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import org.app.backend.modules.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LibraryCardRequestServiceImplTest {

  @Mock private LibraryCardRequestRepository requestRepository;
  @Mock private UserRepository userRepository;

  @InjectMocks private LibraryCardRequestServiceImpl libraryCardRequestService;

  private LibraryCardRequest mockRequest;

  @BeforeEach
  void setUp() {
    mockRequest =
        LibraryCardRequest.builder()
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
  @DisplayName("Get All Requests - Returns list")
  void testGetAllRequests_Success() {
    when(requestRepository.findAll()).thenReturn(List.of(mockRequest));

    List<LibraryCardRequest> result = libraryCardRequestService.getAllRequests();

    assertNotNull(result);
    assertEquals(1, result.size());
    verify(requestRepository, times(1)).findAll();
  }
}
