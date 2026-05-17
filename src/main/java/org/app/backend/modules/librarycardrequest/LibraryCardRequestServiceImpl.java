package org.app.backend.modules.librarycardrequest;

import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LibraryCardRequestServiceImpl implements LibraryCardRequestService {
  LibraryCardRequestRepository requestRepository;

  @Override
  @Transactional
  public LibraryCardRequest createRequest(LibraryCardRequest request) {
    return requestRepository.save(request);
  }

  @Override
  @Transactional(readOnly = true)
  public List<LibraryCardRequest> getAllRequests() {
    return requestRepository.findAll();
  }
}
