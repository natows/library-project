package ug.project.library.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ug.project.library.model.entity.Reservation;
import ug.project.library.model.entity.User;
import ug.project.library.model.entity.Book;
import ug.project.library.model.enumerate.ReservationStatus;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.sql.Timestamp;

@Repository
public class ReservationDao {
    
    private final JdbcTemplate jdbcTemplate;

    public ReservationDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    public List<Reservation> findActiveReservationsByUserId(Long userId) {
        String sql = """
            SELECT r.id, r.status, r.created_at, r.deadline,
                   r.user_id, r.book_id,
                   u.username, u.email, u.encrypted_password, u.user_role,
                   b.title, b.publisher, b.cover_image_url, b.year_published, 
                   b.avg_rating, b.quantity_available
            FROM reservations r
            JOIN users u ON r.user_id = u.id
            JOIN books b ON r.book_id = b.id
            WHERE r.user_id = ?
              AND r.status IN ('OCZEKUJĄCA', 'POTWIERDZONA', 'WYPOŻYCZONA')
            ORDER BY r.created_at DESC
        """;
        
        return jdbcTemplate.query(sql, new ReservationRowMapper(), userId);
    }

    public List<Reservation> findByStatus(ReservationStatus status) {
        String sql = """
            SELECT r.id, r.status, r.created_at, r.deadline,
                   r.user_id, r.book_id
            FROM reservations r
            WHERE r.status = ?
            ORDER BY r.created_at DESC
        """;
        
        return jdbcTemplate.query(sql, new ReservationRowMapper(), status.name());
    }


    private static class ReservationRowMapper implements RowMapper<Reservation> {
        @Override
        public Reservation mapRow(ResultSet rs, int rowNum) throws SQLException {
            Reservation reservation = new Reservation();
            reservation.setId(rs.getLong("id"));
            reservation.setStatus(ReservationStatus.valueOf(rs.getString("status")));
            reservation.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            
            Timestamp deadlineTimestamp = rs.getTimestamp("deadline");
            if (deadlineTimestamp != null) {
                reservation.setDeadline(deadlineTimestamp.toLocalDateTime());
            }
            
            User user = new User();
            user.setId(rs.getLong("user_id"));
            reservation.setUser(user);
            
            Book book = new Book();
            book.setId(rs.getLong("book_id"));
            book.setTitle(rs.getString("title"));
            book.setPublisher(rs.getString("publisher"));
            book.setCoverImageUrl(rs.getString("cover_image_url"));
            reservation.setBook(book);
            
            return reservation;
        }
    }
}