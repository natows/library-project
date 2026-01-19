CREATE TABLE IF NOT EXISTS authors (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255),
    surname VARCHAR(255),
    full_name VARCHAR(511)
);

CREATE TABLE IF NOT EXISTS genres (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) UNIQUE
);

CREATE TABLE IF NOT EXISTS books (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    avg_rating DOUBLE PRECISION,
    year_published INT,
    publisher VARCHAR(255) NOT NULL,
    cover_image_url VARCHAR(512),
    quantity_available INT
);

CREATE TABLE IF NOT EXISTS book_author (
    book_id BIGINT REFERENCES books(id),
    author_id BIGINT REFERENCES authors(id),
    PRIMARY KEY (book_id, author_id)
);

CREATE TABLE IF NOT EXISTS book_genre (
    book_id BIGINT REFERENCES books(id),
    genre_id BIGINT REFERENCES genres(id),
    PRIMARY KEY (book_id, genre_id)
);


CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    encrypted_password VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    user_role VARCHAR(20) NOT NULL
);

CREATE TABLE IF NOT EXISTS reservations (
    id BIGSERIAL PRIMARY KEY,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    deadline TIMESTAMP,
    user_id BIGINT NOT NULL REFERENCES users(id),
    book_id BIGINT NOT NULL REFERENCES books(id)
);

CREATE TABLE IF NOT EXISTS ratings (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    book_id BIGINT NOT NULL REFERENCES books(id),
    score INT NOT NULL CHECK (score >= 1 AND score <= 5),
    created_at TIMESTAMP NOT NULL,
    last_modified_at TIMESTAMP,
    CONSTRAINT unique_user_book_rating UNIQUE (user_id, book_id)
);
 
CREATE TABLE IF NOT EXISTS comments (
    id BIGSERIAL PRIMARY KEY,
    content VARCHAR(1000) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    last_modified_at TIMESTAMP,
    user_id BIGINT NOT NULL REFERENCES users(id),
    book_id BIGINT NOT NULL REFERENCES books(id)
);