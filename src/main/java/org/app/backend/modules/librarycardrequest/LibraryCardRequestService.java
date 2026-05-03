package org.app.backend.modules.librarycardrequest;

import java.util.List;

public interface LibraryCardRequestService {
  LibraryCardRequest createRequest(LibraryCardRequest request);

  List<LibraryCardRequest> getAllRequests();
}
