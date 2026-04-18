package org.app.backend.modules.book;

import java.util.UUID;
import org.app.backend.modules.auth.security.CustomUserDetails;
import org.app.backend.modules.book.dto.*;
import org.app.backend.modules.book.filter.BookFilterDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookService {
  // Lấy danh sách có phân trang và lọc
  Page<BookDTO> findAll(BookFilterDTO filter, Pageable pageable);

  // Lấy chi tiết một đầu sách
  BookDTO findById(UUID id);

  // Tạo mới đầu sách (Chỉ Thủ thư)
  void create(BookCreateDTO dto, CustomUserDetails actor);

  // Cập nhật đầu sách (Chỉ Thủ thư)
  void update(UUID id, BookUpdateDTO dto, CustomUserDetails actor);

  // Xóa mềm đầu sách (Chỉ Thủ thư)
  void delete(UUID id, CustomUserDetails actor);
}
