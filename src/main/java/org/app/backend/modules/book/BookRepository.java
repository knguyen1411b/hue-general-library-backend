package org.app.backend.modules.book;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, UUID>, JpaSpecificationExecutor<Book> {

  // 1. Kiểm tra trùng ISBN (Rất quan trọng cho đầu sách)
  boolean existsByIsbn(String isbn);

  // 2. Kiểm tra trùng ISBN nhưng loại trừ ID hiện tại (Dùng cho Update)
  boolean existsByIsbnAndIdNot(String isbn, UUID id);

  // 3. Kiểm tra xem sách có tồn tại và còn hàng không (Dùng cho logic Mượn sách
  // sau này)
  boolean existsByIdAndCountGreaterThanAndStatus(UUID id, int count, BookStatus status);

  // 4. Tìm kiếm sách theo ID và chưa bị xóa mềm
  Optional<Book> findByIdAndStatusNot(UUID id, BookStatus status);
}
