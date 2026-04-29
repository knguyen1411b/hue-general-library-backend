package org.app.backend.modules.librarycard;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
import java.util.List;

public interface LibraryCardRepository extends JpaRepository<LibraryCard, UUID> {
  List<LibraryCard> findByUserId(UUID userId);
  Page<LibraryCard> findByUserId(UUID userId, Pageable pageable);
  List<LibraryCard> findByStatus(CardStatus status);
  Page<LibraryCard> findByStatus(CardStatus status, Pageable pageable);
}
