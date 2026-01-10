package ug.project.library.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ug.project.library.model.entity.Author;
import java.util.List;

public interface AuthorRepository extends JpaRepository<Author, Long> {
    
    List<Author> findByFullNameContaining(String keyword);

    
    
}
