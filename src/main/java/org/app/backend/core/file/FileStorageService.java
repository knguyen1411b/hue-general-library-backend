package org.app.backend.core.file;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
  String upload(MultipartFile file, String name);
}
