INSERT INTO authors (name, surname, full_name) VALUES 
('J.K.', 'Rowling', 'J.K. Rowling'),
('George', 'Orwell', 'George Orwell')
ON CONFLICT (name, surname) DO NOTHING;

INSERT INTO genres (name) VALUES 
('Fantasy'),
('Science Fiction')
ON CONFLICT (name) DO NOTHING;

