INSERT INTO authors (name, surname, full_name) VALUES 
('J.K.', 'Rowling', 'J.K. Rowling'),
('George', 'Orwell', 'George Orwell'),
('J.R.R.', 'Tolkien', 'J.R.R. Tolkien'),
('Stephen', 'King', 'Stephen King'),
('Agatha', 'Christie', 'Agatha Christie'),
('Fyodor', 'Dostoevsky', 'Fyodor Dostoevsky'),
('Jane', 'Austen', 'Jane Austen'),
('Mark', 'Twain', 'Mark Twain'),
('Ernest', 'Hemingway', 'Ernest Hemingway'),
('Gabriel', 'Garcia Marquez', 'Gabriel Garcia Marquez'),
('Haruki', 'Murakami', 'Haruki Murakami'),
('Virginia', 'Woolf', 'Virginia Woolf'),
('Leo', 'Tolstoy', 'Leo Tolstoy'),
('Charles', 'Dickens', 'Charles Dickens'),
('Toni', 'Morrison', 'Toni Morrison'),
('Franz', 'Kafka', 'Franz Kafka'),
('Hermann', 'Hesse', 'Hermann Hesse'),
('Albert', 'Camus', 'Albert Camus'),
('Oscar', 'Wilde', 'Oscar Wilde'),
('Emily', 'Brontë', 'Emily Brontë')
ON CONFLICT (name, surname) DO NOTHING;

INSERT INTO genres (name) VALUES 
('Fantasy'),
('Science Fiction'),
('Horror'),
('Mystery'),
('Biography'),
('Classic'),
('Adventure'),
('Drama'),
('Philosophy'),
('Romance'),
('Magical Realism'),
('Psychological')
ON CONFLICT (name) DO NOTHING;

INSERT INTO users (username, encrypted_password, email, user_role) VALUES
('user1', '$2a$10$W2Y16uVz3P0fI.N.SgVpmeU3TjM6/L.XGzT3QvO9H/E5.8UvD7T9.', 'user1@example.com', 'USER'),
('user2', '$2a$10$W2Y16uVz3P0fI.N.SgVpmeU3TjM6/L.XGzT3QvO9H/E5.8UvD7T9.', 'user2@example.com', 'USER'),
('user3', '$2a$10$W2Y16uVz3P0fI.N.SgVpmeU3TjM6/L.XGzT3QvO9H/E5.8UvD7T9.', 'user3@example.com', 'USER'),
('user4', '$2a$10$W2Y16uVz3P0fI.N.SgVpmeU3TjM6/L.XGzT3QvO9H/E5.8UvD7T9.', 'user4@example.com', 'USER')
ON CONFLICT (username) DO NOTHING;

INSERT INTO books (title, avg_rating, year_published, publisher, quantity_available, cover_image_url) VALUES
('Harry Potter and the Philosopher''s Stone', 4.8, 1997, 'Bloomsbury', 10, 'https://example.com/hp1.jpg'),
('1984', 4.5, 1949, 'Secker & Warburg', 5, 'https://example.com/1984.jpg'),
('The Hobbit', 4.7, 1937, 'George Allen & Unwin', 8, 'https://example.com/hobbit.jpg'),
('The Shining', 4.6, 1977, 'Doubleday', 4, 'https://example.com/shining.jpg'),
('Murder on the Orient Express', 4.4, 1934, 'Collins Crime Club', 6, 'https://example.com/murder.jpg'),
('Crime and Punishment', 4.7, 1866, 'The Russian Messenger', 3, 'https://example.com/crime.jpg'),
('Pride and Prejudice', 4.8, 1813, 'T. Egerton', 7, 'https://example.com/pride.jpg'),
('The Adventures of Huckleberry Finn', 4.2, 1884, 'Chatto & Windus', 4, 'https://example.com/huck.jpg'),
('The Old Man and the Sea', 4.3, 1952, 'Charles Scribner''s Sons', 5, 'https://example.com/oldman.jpg'),
('One Hundred Years of Solitude', 4.6, 1967, 'Editorial Sudamericana', 2, 'https://example.com/solitude.jpg'),
('Norwegian Wood', 4.1, 1987, 'Kodansha', 6, 'https://example.com/norwegian.jpg'),
('To the Lighthouse', 4.0, 1927, 'Hogarth Press', 3, 'https://example.com/lighthouse.jpg'),
('War and Peace', 4.5, 1869, 'The Russian Messenger', 2, 'https://example.com/war.jpg'),
('Great Expectations', 4.3, 1861, 'Chapman & Hall', 5, 'https://example.com/expectations.jpg'),
('Beloved', 4.4, 1987, 'Alfred A. Knopf', 4, 'https://example.com/beloved.jpg'),
('The Trial', 4.2, 1925, 'Verlag Die Schmiede', 3, 'https://example.com/trial.jpg'),
('Siddhartha', 4.5, 1922, 'S. Fischer Verlag', 8, 'https://example.com/siddhartha.jpg'),
('The Stranger', 4.3, 1942, 'Gallimard', 6, 'https://example.com/stranger.jpg'),
('The Picture of Dorian Gray', 4.4, 1890, 'Lippincott''s Monthly Magazine', 5, 'https://example.com/dorian.jpg'),
('Wuthering Heights', 4.2, 1847, 'Thomas Cautley Newby', 4, 'https://example.com/wuthering.jpg'),
('The Metamorphosis', 4.6, 1915, 'Kurt Wolff Verlag', 5, 'https://example.com/meta.jpg'),
('Anna Karenina', 4.7, 1877, 'The Russian Messenger', 3, 'https://example.com/anna.jpg'),
('Oliver Twist', 4.1, 1837, 'Richard Bentley', 6, 'https://example.com/oliver.jpg'),
('The Great Gatsby', 4.4, 1925, 'Charles Scribner''s Sons', 7, 'https://example.com/gatsby.jpg'),
('Moby-Dick', 3.9, 1851, 'Harper & Brothers', 2, 'https://example.com/moby.jpg')
ON CONFLICT (title) DO NOTHING;

INSERT INTO book_author (book_id, author_id) VALUES
((SELECT id FROM books WHERE title = 'Harry Potter and the Philosopher''s Stone'), (SELECT id FROM authors WHERE full_name = 'J.K. Rowling')),
((SELECT id FROM books WHERE title = '1984'), (SELECT id FROM authors WHERE full_name = 'George Orwell')),
((SELECT id FROM books WHERE title = 'The Hobbit'), (SELECT id FROM authors WHERE full_name = 'J.R.R. Tolkien')),
((SELECT id FROM books WHERE title = 'The Shining'), (SELECT id FROM authors WHERE full_name = 'Stephen King')),
((SELECT id FROM books WHERE title = 'Murder on the Orient Express'), (SELECT id FROM authors WHERE full_name = 'Agatha Christie')),
((SELECT id FROM books WHERE title = 'Crime and Punishment'), (SELECT id FROM authors WHERE full_name = 'Fyodor Dostoevsky')),
((SELECT id FROM books WHERE title = 'Pride and Prejudice'), (SELECT id FROM authors WHERE full_name = 'Jane Austen')),
((SELECT id FROM books WHERE title = 'The Adventures of Huckleberry Finn'), (SELECT id FROM authors WHERE full_name = 'Mark Twain')),
((SELECT id FROM books WHERE title = 'The Old Man and the Sea'), (SELECT id FROM authors WHERE full_name = 'Ernest Hemingway')),
((SELECT id FROM books WHERE title = 'One Hundred Years of Solitude'), (SELECT id FROM authors WHERE full_name = 'Gabriel Garcia Marquez')),
((SELECT id FROM books WHERE title = 'Norwegian Wood'), (SELECT id FROM authors WHERE full_name = 'Haruki Murakami')),
((SELECT id FROM books WHERE title = 'To the Lighthouse'), (SELECT id FROM authors WHERE full_name = 'Virginia Woolf')),
((SELECT id FROM books WHERE title = 'War and Peace'), (SELECT id FROM authors WHERE full_name = 'Leo Tolstoy')),
((SELECT id FROM books WHERE title = 'Great Expectations'), (SELECT id FROM authors WHERE full_name = 'Charles Dickens')),
((SELECT id FROM books WHERE title = 'Beloved'), (SELECT id FROM authors WHERE full_name = 'Toni Morrison')),
((SELECT id FROM books WHERE title = 'The Trial'), (SELECT id FROM authors WHERE full_name = 'Franz Kafka')),
((SELECT id FROM books WHERE title = 'Siddhartha'), (SELECT id FROM authors WHERE full_name = 'Hermann Hesse')),
((SELECT id FROM books WHERE title = 'The Stranger'), (SELECT id FROM authors WHERE full_name = 'Albert Camus')),
((SELECT id FROM books WHERE title = 'The Picture of Dorian Gray'), (SELECT id FROM authors WHERE full_name = 'Oscar Wilde')),
((SELECT id FROM books WHERE title = 'Wuthering Heights'), (SELECT id FROM authors WHERE full_name = 'Emily Brontë')),
((SELECT id FROM books WHERE title = 'The Metamorphosis'), (SELECT id FROM authors WHERE full_name = 'Franz Kafka')),
((SELECT id FROM books WHERE title = 'Anna Karenina'), (SELECT id FROM authors WHERE full_name = 'Leo Tolstoy')),
((SELECT id FROM books WHERE title = 'Oliver Twist'), (SELECT id FROM authors WHERE full_name = 'Charles Dickens'))
ON CONFLICT DO NOTHING;

INSERT INTO book_genre (book_id, genre_id) VALUES
((SELECT id FROM books WHERE title = 'Harry Potter and the Philosopher''s Stone'), (SELECT id FROM genres WHERE name = 'Fantasy')),
((SELECT id FROM books WHERE title = '1984'), (SELECT id FROM genres WHERE name = 'Science Fiction')),
((SELECT id FROM books WHERE title = 'The Hobbit'), (SELECT id FROM genres WHERE name = 'Fantasy')),
((SELECT id FROM books WHERE title = 'The Shining'), (SELECT id FROM genres WHERE name = 'Horror')),
((SELECT id FROM books WHERE title = 'Murder on the Orient Express'), (SELECT id FROM genres WHERE name = 'Mystery')),
((SELECT id FROM books WHERE title = 'Crime and Punishment'), (SELECT id FROM genres WHERE name = 'Classic')),
((SELECT id FROM books WHERE title = 'Crime and Punishment'), (SELECT id FROM genres WHERE name = 'Psychological')),
((SELECT id FROM books WHERE title = 'Pride and Prejudice'), (SELECT id FROM genres WHERE name = 'Classic')),
((SELECT id FROM books WHERE title = 'Pride and Prejudice'), (SELECT id FROM genres WHERE name = 'Romance')),
((SELECT id FROM books WHERE title = 'The Adventures of Huckleberry Finn'), (SELECT id FROM genres WHERE name = 'Adventure')),
((SELECT id FROM books WHERE title = 'The Old Man and the Sea'), (SELECT id FROM genres WHERE name = 'Classic')),
((SELECT id FROM books WHERE title = 'One Hundred Years of Solitude'), (SELECT id FROM genres WHERE name = 'Magical Realism')),
((SELECT id FROM books WHERE title = 'Norwegian Wood'), (SELECT id FROM genres WHERE name = 'Drama')),
((SELECT id FROM books WHERE title = 'To the Lighthouse'), (SELECT id FROM genres WHERE name = 'Classic')),
((SELECT id FROM books WHERE title = 'War and Peace'), (SELECT id FROM genres WHERE name = 'Classic')),
((SELECT id FROM books WHERE title = 'Great Expectations'), (SELECT id FROM genres WHERE name = 'Classic')),
((SELECT id FROM books WHERE title = 'Beloved'), (SELECT id FROM genres WHERE name = 'Drama')),
((SELECT id FROM books WHERE title = 'The Trial'), (SELECT id FROM genres WHERE name = 'Philosophy')),
((SELECT id FROM books WHERE title = 'Siddhartha'), (SELECT id FROM genres WHERE name = 'Philosophy')),
((SELECT id FROM books WHERE title = 'The Stranger'), (SELECT id FROM genres WHERE name = 'Philosophy')),
((SELECT id FROM books WHERE title = 'The Picture of Dorian Gray'), (SELECT id FROM genres WHERE name = 'Classic')),
((SELECT id FROM books WHERE title = 'Wuthering Heights'), (SELECT id FROM genres WHERE name = 'Classic')),
((SELECT id FROM books WHERE title = 'The Metamorphosis'), (SELECT id FROM genres WHERE name = 'Classic')),
((SELECT id FROM books WHERE title = 'Anna Karenina'), (SELECT id FROM genres WHERE name = 'Classic')),
((SELECT id FROM books WHERE title = 'Oliver Twist'), (SELECT id FROM genres WHERE name = 'Classic'))
ON CONFLICT DO NOTHING;

INSERT INTO comments (content, created_at, user_id, book_id) VALUES
('Great book, highly recommend!', CURRENT_TIMESTAMP, (SELECT id FROM users WHERE username = 'user1'), (SELECT id FROM books WHERE title = 'Harry Potter and the Philosopher''s Stone')),
('A classic masterpiece.', CURRENT_TIMESTAMP, (SELECT id FROM users WHERE username = 'user2'), (SELECT id FROM books WHERE title = '1984')),
('I love the world building.', CURRENT_TIMESTAMP, (SELECT id FROM users WHERE username = 'user3'), (SELECT id FROM books WHERE title = 'The Hobbit')),
('Very scary but good.', CURRENT_TIMESTAMP, (SELECT id FROM users WHERE username = 'user4'), (SELECT id FROM books WHERE title = 'The Shining')),
('Best mystery ever.', CURRENT_TIMESTAMP, (SELECT id FROM users WHERE username = 'user1'), (SELECT id FROM books WHERE title = 'Murder on the Orient Express'))
ON CONFLICT DO NOTHING;

INSERT INTO ratings (score, created_at, user_id, book_id) VALUES
(5, CURRENT_TIMESTAMP, (SELECT id FROM users WHERE username = 'user1'), (SELECT id FROM books WHERE title = 'Harry Potter and the Philosopher''s Stone')),
(5, CURRENT_TIMESTAMP, (SELECT id FROM users WHERE username = 'user2'), (SELECT id FROM books WHERE title = '1984')),
(4, CURRENT_TIMESTAMP, (SELECT id FROM users WHERE username = 'user3'), (SELECT id FROM books WHERE title = 'The Hobbit')),
(5, CURRENT_TIMESTAMP, (SELECT id FROM users WHERE username = 'user4'), (SELECT id FROM books WHERE title = 'The Shining')),
(4, CURRENT_TIMESTAMP, (SELECT id FROM users WHERE username = 'user1'), (SELECT id FROM books WHERE title = 'Murder on the Orient Express'))
ON CONFLICT DO NOTHING;

INSERT INTO reservations (status, created_at, deadline, user_id, book_id) VALUES
('OCZEKUJĄCA', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '14 days', (SELECT id FROM users WHERE username = 'user1'), (SELECT id FROM books WHERE title = 'Harry Potter and the Philosopher''s Stone')),
('POTWIERDZONA', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '14 days', (SELECT id FROM users WHERE username = 'user2'), (SELECT id FROM books WHERE title = '1984')),
('WYPOŻYCZONA', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '14 days', (SELECT id FROM users WHERE username = 'user3'), (SELECT id FROM books WHERE title = 'The Hobbit')),
('ZWRÓCONA', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '14 days', (SELECT id FROM users WHERE username = 'user4'), (SELECT id FROM books WHERE title = 'The Shining')),
('OCZEKUJĄCA', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '14 days', (SELECT id FROM users WHERE username = 'user1'), (SELECT id FROM books WHERE title = 'Murder on the Orient Express'))
ON CONFLICT DO NOTHING;

