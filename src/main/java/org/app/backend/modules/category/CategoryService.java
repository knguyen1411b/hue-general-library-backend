package org.app.backend.modules.category;

import java.util.UUID;
import org.app.backend.modules.auth.security.CustomUserDetails;
import org.app.backend.modules.category.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CategoryService {

  // Ai cũng xem được danh sách thể loại (kể cả User chưa đăng nhập)
  Page<CategoryDTO> findAll(CategoryFilterDTO filter, Pageable pageable);

  // Lấy chi tiết 1 thể loại
  CategoryDTO findById(UUID id);

  // --- CÁC HÀM CRUD DÀNH CHO THỦ THƯ ---

  void create(CategoryCreateDTO dto, CustomUserDetails actor);

  void update(UUID id, CategoryUpdateDTO dto, CustomUserDetails actor);

  void delete(UUID id, CustomUserDetails actor);
}
