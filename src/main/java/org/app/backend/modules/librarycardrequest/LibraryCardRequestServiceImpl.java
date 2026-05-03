package org.app.backend.modules.librarycardrequest;

import java.util.List;
import org.app.backend.modules.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LibraryCardRequestServiceImpl implements LibraryCardRequestService {
  private final LibraryCardRequestRepository requestRepository;
  private final UserRepository userRepository;

  public LibraryCardRequestServiceImpl(
      LibraryCardRequestRepository requestRepository, UserRepository userRepository) {
    this.requestRepository = requestRepository;
    this.userRepository = userRepository;
  }

  @Override
  public LibraryCardRequest createRequest(LibraryCardRequest request) {
    return requestRepository.save(request);
  }

  @Override
  public List<LibraryCardRequest> getAllRequests() {
    return requestRepository.findAll();
  }
}
