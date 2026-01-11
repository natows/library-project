package ug.project.library.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import ug.project.library.model.entity.User;
import ug.project.library.model.enumerate.UserRole;
import ug.project.library.repository.UserRepository;

@Configuration
public class DataInitializer {
    
    @Bean
    CommandLineRunner initUsers(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.findByUsername("nati").isEmpty()) {
                User user = new User(
                    "nati",
                    passwordEncoder.encode("nati_pass"), 
                    "mail@mail.com",
                    UserRole.USER
                );
                userRepository.save(user);
                System.out.println("Created user: nati / nati_pass");
            }

            if (userRepository.findByUsername("admin").isEmpty()) {
                User admin = new User(
                    "admin",
                    passwordEncoder.encode("admin123"),
                    "admin@library.com",
                    UserRole.ADMIN
                );
                userRepository.save(admin);
                System.out.println("Created admin: admin / admin123");
            }
        };
    }
}