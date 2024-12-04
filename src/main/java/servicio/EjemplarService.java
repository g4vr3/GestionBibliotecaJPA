package servicio;

import modelo.Ejemplar;
import modelo.Libro;
import repositorio.GenericDAO;
import util.Validator;

import java.util.List;

public class EjemplarService {
    private final GenericDAO<Ejemplar> ejemplarDAO;
    private final List<Ejemplar> ejemplares;
    private final LibroService libroService;

    public EjemplarService(GenericDAO<Ejemplar> ejemplarDAO, LibroService libroService) {
        this.ejemplarDAO = ejemplarDAO;
        this.ejemplares = ejemplarDAO.readAll();
        this.libroService = libroService;
    }

    public void registrar(String isbn13, String estado) {
        // Validación de campos requeridos
        if (Validator.isNotFilled(isbn13))
            throw new IllegalArgumentException("El ISBN13 es obligatorio");

        // Validación del estado introducido,
        // si no se especifica, se establece como disponible
        if (Validator.isNotFilled(estado))
            estado = "Disponible";

        // Validación de campos válidos
        if (!Validator.isIsbn13Valid(isbn13))
            throw new IllegalArgumentException("ISBN no válido");
        if (!Validator.isEstadoValid(estado))
            throw new IllegalArgumentException("Estado del ejemplar no válido");
        if (estado.equalsIgnoreCase("Prestado"))
            throw new IllegalArgumentException("No se puede registrar un ejemplar como prestado.");

        // Obtener registro del libro y validar que el libro exista
        Libro libroRef = libroService.read(isbn13);
        if (libroRef == null)
            throw new IllegalArgumentException("No se ha encontrado el libro para el que quiere registrar un ejemplar. Debe registrar el libro primero.");

        Ejemplar ejemplarToCreate = new Ejemplar(libroRef, estado);
        ejemplarDAO.create(ejemplarToCreate); // Crear en DB
        ejemplares.add(ejemplarToCreate); // Añadir a la lista de memoria
        libroRef.getEjemplares().add(ejemplarToCreate); // Añadir el ejemplar al libro
    }

    // Retornar ejemplar por ID
    public Ejemplar read(int idEjemplar) {
        return ejemplares.stream()
                .filter(ejemplar -> ejemplar.getId().equals(idEjemplar))
                .findFirst()
                .orElse(null);
    }

    public void update(Ejemplar ejemplarRef) {
        ejemplarDAO.update(ejemplarRef);
    }

    // Obtener número de ejemplares disponibles en total
    public int getStockTotalEjemplares() {
        return (int) ejemplares.stream()
               .filter(e -> e.getEstado().equalsIgnoreCase("Disponible"))
               .count();
    }
}
