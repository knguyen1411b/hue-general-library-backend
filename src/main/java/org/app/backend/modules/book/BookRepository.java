package org.app.backend.modules.book;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
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

  Integer findCountByIdAndStatus(UUID id, BookStatus status);

  @Modifying
  @Query("UPDATE Book b SET b.count = :count WHERE b.id = :id")
  int updateCountById(UUID id, Integer count);

  @Modifying
  @Query("UPDATE Book b SET b.count = b.count - 1 WHERE b.id = :id AND b.count > 0")
  int decreaseCount(UUID id);

  @Modifying
  @Query("UPDATE Book b SET b.count = b.count + 1 WHERE b.id = :id")
  int increaseCount(UUID id);

}
