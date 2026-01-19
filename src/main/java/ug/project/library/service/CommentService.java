package ug.project.library.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ug.project.library.dto.CommentDto;
import ug.project.library.model.entity.Book;
import ug.project.library.model.entity.Comment;
import ug.project.library.model.entity.User;
import ug.project.library.repository.BookRepository;
import ug.project.library.repository.CommentRepository;
import ug.project.library.repository.UserRepository;

import java.time.LocalDateTime;

@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    public CommentService(CommentRepository commentRepository, UserRepository userRepository, BookRepository bookRepository){
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
    }

    @Transactional(readOnly = true)
    public Page<CommentDto> getAllComments(Pageable pageable) {
        return commentRepository.findAll(pageable).map(this::mapToDto);
    }

    @Transactional(readOnly = true)
    public CommentDto getCommentById(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        return mapToDto(comment);
    }

    @Transactional
    public CommentDto addComment(CommentDto commentDto){
        User user = userRepository.findById(commentDto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Book book = bookRepository.findById(commentDto.getBookId())
                .orElseThrow(() -> new RuntimeException("Book not found"));

        Comment comment = new Comment(commentDto.getContent(), user, book);
        Comment saved = commentRepository.save(comment);
        return mapToDto(saved);
    }

    @Transactional
    public CommentDto updateComment(Long id, CommentDto commentDto) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        comment.setContent(commentDto.getContent());
        comment.setLastModifiedAt(LocalDateTime.now());
        return mapToDto(commentRepository.save(comment));
    }

    @Transactional
    public void deleteComment(Long id) {
        if (!commentRepository.existsById(id)) {
            throw new RuntimeException("Comment not found");
        }
        commentRepository.deleteById(id);
    }

    private CommentDto mapToDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getContent(),
                comment.getCreatedAt(),
                comment.getLastModifiedAt(),
                comment.getUser().getId(),
                comment.getUser().getUsername(),
                comment.getBook().getId(),
                comment.getBook().getTitle()
        );
    }
}