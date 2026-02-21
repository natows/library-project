DROP TABLE IF EXISTS authors CASCADE;
CREATE TABLE authors (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255),
    surname VARCHAR(255),
    full_name VARCHAR(511),
    CONSTRAINT unique_author UNIQUE (name, surname)
);

DROP TABLE IF EXISTS genres CASCADE;
CREATE TABLE genres (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) UNIQUE
);

DROP TABLE IF EXISTS books CASCADE;
CREATE TABLE books (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) UNIQUE NOT NULL,
    avg_rating DOUBLE PRECISION,
    year_published INT,
    publisher VARCHAR(255) NOT NULL,
    cover_image_url VARCHAR(512),
    quantity_available INT
);

DROP TABLE IF EXISTS book_author CASCADE;
CREATE TABLE book_author (
    book_id BIGINT REFERENCES books(id),
    author_id BIGINT REFERENCES authors(id),
    PRIMARY KEY (book_id, author_id)
);

DROP TABLE IF EXISTS book_genre CASCADE;
CREATE TABLE book_genre (
    book_id BIGINT REFERENCES books(id),
    genre_id BIGINT REFERENCES genres(id),
    PRIMARY KEY (book_id, genre_id)
);


DROP TABLE IF EXISTS users CASCADE;
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    encrypted_password VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    user_role VARCHAR(20) NOT NULL
);

DROP TABLE IF EXISTS reservations CASCADE;
CREATE TABLE reservations (
    id BIGSERIAL PRIMARY KEY,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    deadline TIMESTAMP,
    user_id BIGINT NOT NULL REFERENCES users(id),
    book_id BIGINT NOT NULL REFERENCES books(id)
);

DROP TABLE IF EXISTS ratings CASCADE;
CREATE TABLE ratings (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    book_id BIGINT NOT NULL REFERENCES books(id),
    score INT NOT NULL CHECK (score >= 1 AND score <= 5),
    created_at TIMESTAMP NOT NULL,
    last_modified_at TIMESTAMP,
    CONSTRAINT unique_user_book_rating UNIQUE (user_id, book_id)
);
 
DROP TABLE IF EXISTS comments CASCADE;
CREATE TABLE comments (
    id BIGSERIAL PRIMARY KEY,
    content VARCHAR(1000) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    last_modified_at TIMESTAMP,
    user_id BIGINT NOT NULL REFERENCES users(id),
    book_id BIGINT NOT NULL REFERENCES books(id)
);