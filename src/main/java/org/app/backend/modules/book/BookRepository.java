package org.app.backend.modules.book;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, UUID>, JpaSpecificationExecutor<Book> {

  boolean existsByIsbn(String isbn);

  @Modifying
  @Query("UPDATE Book b SET b.count = b.count - 1 WHERE b.id = :id AND b.count > 0")
  int decreaseCount(UUID id);

  @Modifying
  @Query("UPDATE Book b SET b.count = b.count + 1 WHERE b.id = :id")
  void increaseCount(UUID id);
}
