package org.app.backend.modules.category;

import java.util.UUID;
import org.app.backend.modules.auth.security.CustomUserDetails;
import org.app.backend.modules.category.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CategoryService {
  Page<CategoryDTO> findAll(CategoryFilterDTO filter, Pageable pageable);

  CategoryDTO findById(UUID id);

  void create(CategoryCreateDTO dto, CustomUserDetails actor);

  void update(UUID id, CategoryUpdateDTO dto, CustomUserDetails actor);

  void delete(UUID id, CustomUserDetails actor);
}
