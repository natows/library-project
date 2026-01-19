package ug.project.library.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ug.project.library.model.entity.Author;
import ug.project.library.model.entity.Book;
import ug.project.library.model.entity.User;
import ug.project.library.repository.AuthorRepository;
import ug.project.library.repository.BookRepository;
import ug.project.library.repository.UserRepository;

@Service
public class StatisticsService {

    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final AuthorRepository authorRepository;

    public StatisticsService(BookRepository bookRepository, UserRepository userRepository, AuthorRepository authorRepository) {
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.authorRepository = authorRepository;
    }


    @Transactional(readOnly = true)
    public Page<Book> getMostPopularBooks(Pageable pageable){
        return bookRepository.findMostPopularBooks(pageable);

    }

    @Transactional(readOnly = true)
    public Page<User> getUsersWithMostLoans(Pageable pageable) {
        return userRepository.getUsersWithMostLoans(pageable);
    }


    @Transactional(readOnly = true) 
    public Page<Author> getMostPopularAuthors(Pageable pageable) {
        return authorRepository.findMostPopularAuthors(pageable);
    }

    
    
}
