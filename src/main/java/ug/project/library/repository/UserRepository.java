package ug.project.library.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ug.project.library.model.entity.User;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    
    Page<User> findByUsernameContaining(String keyword, Pageable pageable);

    Optional<User> findByUsername(String username); 
    
    Optional<User> findByEmail(String email);

    
    
}
