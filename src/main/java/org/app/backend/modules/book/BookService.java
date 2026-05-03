package org.app.backend.modules.book;

import java.util.UUID;
import org.app.backend.modules.auth.security.CustomUserDetails;
import org.app.backend.modules.book.dto.*;
import org.app.backend.modules.book.dto.BookFilterDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookService {
  Page<BookDTO> findAll(BookFilterDTO filter, Pageable pageable);

  BookDTO findById(UUID id);

  void create(BookCreateDTO dto, CustomUserDetails actor);

  void update(UUID id, BookUpdateDTO dto, CustomUserDetails actor);

  void delete(UUID id, CustomUserDetails actor);
}
