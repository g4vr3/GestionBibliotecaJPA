package servicio;

import modelo.Libro;
import repositorio.GenericDAO;
import util.Validator;

import java.util.List;

public class LibroService {
    private final GenericDAO<Libro> libroDAO;
    private final List<Libro> libros;

    public LibroService(GenericDAO<Libro> libroDAO) {
        this.libroDAO = libroDAO;
        this.libros = libroDAO.readAll();
    }

    public void registrar(String isbn13, String titulo, String autor) {

        // Validación de campos requeridos
        if (Validator.isNotFilled(isbn13, titulo, autor))
            throw new IllegalArgumentException("Todos los campos son obligatorios");

        // Verificación de duplicidad de libro
        Libro libroExistente = read(isbn13);
        if (libroExistente != null)
            throw new IllegalArgumentException("Este libro ya existe");

        // Validación de campos
        if (!Validator.isIsbn13Valid(isbn13))
            throw new IllegalArgumentException("ISBN13 no válido");

        Libro libroToCreate = new Libro(isbn13, titulo, autor);
        libroDAO.create(libroToCreate); // Crear en DB
        libros.add(libroToCreate); // Añadir a la lista de memoria
    }

    // Retorna libro con el ISBN13 introducido
    public Libro read(String isbn13) {
        return libros.stream()
                .filter(libro -> libro.getIsbn().equals(isbn13))
                .findFirst()
                .orElse(null);
    }

    public List<Libro> getLibros() {
        return libros;
    }
}
