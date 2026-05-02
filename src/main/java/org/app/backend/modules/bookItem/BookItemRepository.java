package org.app.backend.modules.bookItem;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface BookItemRepository
    extends JpaRepository<BookItem, UUID>, JpaSpecificationExecutor<BookItem> {
  Optional<BookItem> findByBarcode(String barcode);

  boolean existsByBarcode(String barcode);
}
