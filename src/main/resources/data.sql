INSERT INTO authors (name, surname) VALUES 
('J.K.', 'Rowling'),
('George', 'Orwell')
ON CONFLICT DO NOTHING;

INSERT INTO genres (name) VALUES 
('Fantasy'),
('Science Fiction')
ON CONFLICT (name) DO NOTHING;

