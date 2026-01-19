package ug.project.library.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ug.project.library.model.entity.Book;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookRepository extends JpaRepository<Book, Long> {
    //gotowe metody save findbyid delete findall

       Page<Book> findByTitleContaining(String keyword, Pageable pageable);
       Page<Book> findByPublisher(String keyword, Pageable pageable);

       @Query("SELECT b FROM Book b ORDER BY b.avgRating DESC")
       List<Book> findTopRatedBooks(Pageable pageable);


       @Query("SELECT DISTINCT b FROM Book b " +
       "LEFT JOIN b.authors a " +
       "LEFT JOIN b.genres g " +
       "WHERE (:title IS NULL OR :title = '' OR LOWER(b.title) LIKE LOWER(CONCAT('%', :title, '%'))) " +
       "AND (:author IS NULL OR :author = '' OR LOWER(COALESCE(a.fullName, '')) LIKE LOWER(CONCAT('%', :author, '%'))) " +
       "AND (:keyword IS NULL OR :keyword = '' OR LOWER(COALESCE(g.name, '')) LIKE LOWER(CONCAT('%', :keyword, '%')))")
       Page<Book> searchBooks(@Param("title") String title, 
                     @Param("author") String author, 
                     @Param("keyword") String keyword, 
                     Pageable pageable);


       @Query("""
       SELECT b FROM Book b
       JOIN b.authors a
       WHERE LOWER(b.title) = LOWER(:title)
       AND a.id IN :authorIds
       GROUP BY b.id
       HAVING COUNT(DISTINCT a.id) = :authorCount
       AND COUNT(DISTINCT a.id) = (SELECT COUNT(a2.id) FROM Book b2 JOIN b2.authors a2 WHERE b2.id = b.id)
       """)
       List<Book> findByTitleAndAuthors(@Param("title") String title, 
                                        @Param("authorIds") List<Long> authorIds, 
                                        @Param("authorCount") long authorCount);

       @Query("""
       SELECT r.book
       FROM Reservation r
       WHERE r.status IN (
       ug.project.library.model.enumerate.ReservationStatus.WYPOŻYCZONA,
       ug.project.library.model.enumerate.ReservationStatus.ZWRÓCONA
       )
       GROUP BY r.book
       ORDER BY COUNT(r.id) DESC
       """)
       Page<Book> findMostPopularBooks(Pageable pageable);
}
