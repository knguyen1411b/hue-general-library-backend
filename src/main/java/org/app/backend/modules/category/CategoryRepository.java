package org.app.backend.modules.category;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository
    extends JpaRepository<Category, UUID>, JpaSpecificationExecutor<Category> {

  // Kiểm tra xem tên Thể loại đã tồn tại chưa (Dùng khi Tạo mới)
  boolean existsByTitle(String title);

  // Kiểm tra trùng tên nhưng bỏ qua chính nó (Dùng khi Cập nhật)
  boolean existsByTitleAndIdNot(String title, UUID id);
}
