package ug.project.library.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ug.project.library.model.entity.Book;
import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {
    //gotowe metody save findbyid delete findall

    List<Book> findByTitleContaining(String keyword);

    List<Book> findByAuthorsContaining(String keyword);

    List<Book> findByGenresContaining(String keyword);
    
}
