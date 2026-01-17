package ug.project.library.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ug.project.library.model.entity.Book;
import java.util.List;
import org.springframework.data.jpa.repository.Query;

public interface BookRepository extends JpaRepository<Book, Long> {
    //gotowe metody save findbyid delete findall

    Page<Book> findByTitleContaining(String keyword, Pageable pageable);
    Page<Book> findByPublisher(String keyword, Pageable pageable);

    @Query("SELECT b FROM Book b ORDER BY b.avgRating DESC")
    List<Book> findTopRatedBooks(Pageable pageable);
    
}
