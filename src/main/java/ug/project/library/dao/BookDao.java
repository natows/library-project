package ug.project.library.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ug.project.library.model.entity.Book;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class BookDao {
    
    private final JdbcTemplate jdbcTemplate;

    public BookDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Book> findBooksByAuthorAndGenre(String authorName, String genreName) {
        String sql = """
            SELECT DISTINCT b.* 
            FROM books b
            JOIN book_author ba ON b.id = ba.book_id
            JOIN authors a ON ba.author_id = a.id
            JOIN book_genre bg ON b.id = bg.book_id
            JOIN genres g ON bg.genre_id = g.id
            WHERE a.full_name LIKE ? AND g.name = ?
            """;
        
        return jdbcTemplate.query(sql, new BookRowMapper(), "%" + authorName + "%", genreName);
    }

    public int deincrementQuantityAvailable(Long bookId) {
        String sql = "UPDATE books SET quantity_available = quantity_available - 1 WHERE id = ?";
        return jdbcTemplate.update(sql, bookId);
    }

    public int incrementBookQuantity(Long bookId) {
        String sql = "UPDATE books SET quantity_available = quantity_available + 1 WHERE id = ?";
        return jdbcTemplate.update(sql, bookId);
    }

    private static class BookRowMapper implements RowMapper<Book> {
        @Override
        public Book mapRow(ResultSet rs, int rowNum) throws SQLException {
            Book book = new Book();
            book.setId(rs.getLong("id"));
            book.setTitle(rs.getString("title"));
            book.setAvgRating(rs.getDouble("avg_rating"));
            book.setYearPublished(rs.getInt("year_published"));
            book.setPublisher(rs.getString("publisher"));
            book.setCoverImageUrl(rs.getString("cover_image_url"));
            book.setQuantityAvailable(rs.getInt("quantity_available"));
            return book;
        }
    }
}