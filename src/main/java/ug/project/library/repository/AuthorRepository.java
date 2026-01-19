package ug.project.library.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ug.project.library.model.entity.Author;
import ug.project.library.model.entity.Book;
import org.springframework.data.jpa.repository.Query;


import java.util.List;
import java.util.Optional;

public interface AuthorRepository extends JpaRepository<Author, Long> {
    
    List<Author> findByFullNameContaining(String keyword);
    
    Optional<Author> findByNameAndSurname(String name, String surname);

    @Query("""
        SELECT a
        FROM Author a
        JOIN a.books b
        JOIN Reservation r ON r.book = b
        WHERE r.status IN ('WYPOŻYCZONA', 'ZWRÓCONA')
        GROUP BY a
        ORDER BY COUNT(r.id) DESC
    """)
    Page<Author> findMostPopularAuthors(Pageable pageable);

    


    
    
}
