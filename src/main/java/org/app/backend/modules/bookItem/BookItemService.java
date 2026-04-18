package org.app.backend.modules.bookItem;

import java.util.UUID;
import org.app.backend.modules.auth.security.CustomUserDetails;
import org.app.backend.modules.bookItem.dto.*;
import org.app.backend.modules.bookItem.filter.BookItemFilterDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookItemService {
  Page<BookItemDTO> findAll(BookItemFilterDTO filter, Pageable pageable);

  BookItemDTO findById(UUID id);

  void create(BookItemCreateDTO dto, CustomUserDetails actor);

  void update(UUID id, BookItemUpdateDTO dto, CustomUserDetails actor);

  void delete(UUID id, CustomUserDetails actor);
}
