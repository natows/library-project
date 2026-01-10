package ug.project.library.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ug.project.library.model.entity.User;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    
    Page<User> findByUsernameContaining(String keyword, Pageable pageable);

    
    
}
