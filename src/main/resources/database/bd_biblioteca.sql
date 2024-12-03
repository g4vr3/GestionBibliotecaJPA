CREATE DATABASE biblioteca;
USE biblioteca;
-- Crear tablas
CREATE TABLE Usuario (
    id INT AUTO_INCREMENT PRIMARY KEY,
    dni VARCHAR(15) NOT NULL UNIQUE,
    nombre VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    tipo ENUM('normal', 'administrador') NOT NULL,
    penalizacionHasta DATE NULL
);

CREATE TABLE Libro (
    isbn VARCHAR(20) PRIMARY KEY,
    titulo VARCHAR(200) NOT NULL,
    autor VARCHAR(100) NOT NULL
);

CREATE TABLE Ejemplar (
    id INT AUTO_INCREMENT PRIMARY KEY,
    isbn VARCHAR(20) NOT NULL,
    estado ENUM('Disponible', 'Prestado', 'Dañado') DEFAULT 'Disponible',
    FOREIGN KEY (isbn) REFERENCES Libro(isbn) ON DELETE CASCADE
);

CREATE TABLE Prestamo (
    id INT AUTO_INCREMENT PRIMARY KEY,
    usuario_id INT NOT NULL,
    ejemplar_id INT NOT NULL,
    fechaInicio DATE NOT NULL,
    fechaDevolucion DATE NULL,
    FOREIGN KEY (usuario_id) REFERENCES Usuario(id) ON DELETE CASCADE,
    FOREIGN KEY (ejemplar_id) REFERENCES Ejemplar(id) ON DELETE CASCADE
);

-- Insertar datos de prueba
-- Usuarios
INSERT INTO Usuario (dni, nombre, email, password, tipo) VALUES 
('12345678A', 'Juan Pérez', 'juan.perez@example.com', 'password123', 'normal'),
('87654321B', 'Ana García', 'ana.garcia@example.com', 'password123', 'normal'),
('11223344C', 'Luis López', 'luis.lopez@example.com', 'adminpass123', 'administrador');

-- Libros
INSERT INTO Libro (isbn, titulo, autor) VALUES 
('9781234567890', 'El Quijote', 'Miguel de Cervantes'),
('9789876543210', 'Cien Años de Soledad', 'Gabriel García Márquez'),
('9781111111111', '1984', 'George Orwell');

-- Ejemplares
INSERT INTO Ejemplar (isbn, estado) VALUES 
('9781234567890', 'Disponible'), ('9781234567890', 'Disponible'), ('9781234567890', 'Prestado'), -- Ejemplares de "El Quijote"
('9789876543210', 'Disponible'), ('9789876543210', 'Dañado'), -- Ejemplares de "Cien Años de Soledad"
('9781111111111', 'Disponible'), ('9781111111111', 'Disponible'); -- Ejemplares de "1984"

-- Préstamos
INSERT INTO Prestamo (usuario_id, ejemplar_id, fechaInicio, fechaDevolucion) VALUES 
(1, 3, '2024-11-01', NULL), -- Juan Pérez tiene un ejemplar prestado de "El Quijote"
(2, 4, '2024-11-05', '2024-11-20'); -- Ana García devolvió un ejemplar de "Cien Años de Soledad"
