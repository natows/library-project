package ug.project.library.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ug.project.library.model.entity.Genre;
import java.util.List;

public interface GenreRepository extends JpaRepository<Genre, Long> {
    
    List<Genre> findByNameContaining(String keyword);

    
    
}
