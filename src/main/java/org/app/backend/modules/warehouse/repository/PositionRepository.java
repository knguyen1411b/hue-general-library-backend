package org.app.backend.modules.warehouse.repository;

import org.app.backend.modules.warehouse.entity.Position;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PositionRepository extends JpaRepository<Position, Long> {
    // Hàm này giúp lấy các ngăn của 1 kệ theo đúng thứ tự hàng/cột
    List<Position> findByShelfIdOrderByRowIndexAscColIndexAsc(Long shelfId);
}