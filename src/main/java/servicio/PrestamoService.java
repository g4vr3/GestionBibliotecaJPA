package servicio;

import modelo.Ejemplar;
import modelo.Prestamo;
import modelo.Usuario;
import repositorio.GenericDAO;
import util.Validator;

import java.time.LocalDate;
import java.util.List;

public class PrestamoService {
    private final GenericDAO<Prestamo> prestamoDAO;
    private final List<Prestamo> prestamos;
    private final UsuarioService usuarioService;
    private final EjemplarService ejemplarService;

    public PrestamoService(GenericDAO<Prestamo> prestamoDAO, UsuarioService usuarioService, EjemplarService ejemplarService) {
        this.prestamoDAO = prestamoDAO;
        this.prestamos = prestamoDAO.readAll();
        this.usuarioService = usuarioService;
        this.ejemplarService = ejemplarService;
    }

    public void registrar(int idUsuario, int idEjemplar) {
        // Validación de campos requeridos
        if (Validator.isNotFilled(idUsuario, idEjemplar))
            throw new IllegalArgumentException("Todos los campos son obligatorios");

        // Validación de existencia de usuario y ejemplar
        Usuario usuarioRef = usuarioService.read(idUsuario);
        Ejemplar ejemplarRef = ejemplarService.read(idEjemplar);
        if (usuarioRef == null)
            throw new IllegalArgumentException("No hay un usuario registrado con este ID");
        if (ejemplarRef == null)
            throw new IllegalArgumentException("No hay un ejemplar registrado con este ID");

        // Verificación de penalización del usuario
        if (Validator.isUsuarioPenalizado(usuarioRef))
            throw new IllegalArgumentException("El usuario está penalizado hasta: " + usuarioRef.getPenalizacionHasta().toString());

        // Validación de límite de préstamos activos del usuario
        if (Validator.hasUsuarioLimitePrestamosActivos(usuarioRef))
            throw new IllegalArgumentException("El usuario ha superado su límite de préstamos activos");

        // Validación de disponibilidad del ejemplar
        if (!Validator.isEjemplarDisponible(ejemplarRef))
            throw new IllegalArgumentException("Este ejemplar no está disponible");

        // Registrar prestamo
        Prestamo prestamoToCreate = new Prestamo(usuarioRef, ejemplarRef, LocalDate.now(), null);
        prestamoDAO.create(prestamoToCreate); // Crear en DB
        prestamos.add(prestamoToCreate); // Añadir a la lista de memoria

        // Actualizar estado del ejemplar
        ejemplarRef.setEstado("Prestado");
        ejemplarService.update(ejemplarRef); // Actualizar en DB
    }

    public void devolver(int idPrestamo) {
        if (Validator.isNotFilled(idPrestamo))
            throw new IllegalArgumentException("Todos los campos son obligatorios");

        // Validación de existencia del préstamo
        Prestamo prestamoToDevolver = readById(idPrestamo);
        if (prestamoToDevolver == null)
            throw new IllegalArgumentException("No hay un préstamo registrado con este ID");

        // Validación de que el préstamo no tenga fecha de devolución
        if (!Validator.isPrestamoDevuelto(prestamoToDevolver))
            throw new IllegalArgumentException("Este préstamo ya ha sido devuelto");

        // Actualizar estado del ejemplar
        Ejemplar ejemplarRef = prestamoToDevolver.getEjemplar();
        ejemplarRef.setEstado("Disponible");
        ejemplarService.update(ejemplarRef); // Actualizar en DB

        // Actualizar fecha de devolución en el préstamo
        prestamoToDevolver.setFechaDevolucion(LocalDate.now());
        update(prestamoToDevolver); // Actualizar en DB

        // Verificar que se haya devuelto en plazo
        if (!Validator.isPrestamoDevueltoEnPlazo(prestamoToDevolver)) {
            Usuario usuarioRef = prestamoToDevolver.getUsuario(); // Obtiene el usuario vinculado al préstamo
            usuarioService.penalizarUsuario(usuarioRef); // Penaliza al usuario
            throw new RuntimeException("El préstamo no ha sido devuelto en plazo. Se ha penalizado al usuario");
        }
    }

    // Retorna préstamo con el ID introducido
    public Prestamo readById(int idPrestamo) {
        return prestamos.stream()
                .filter(prestamo -> prestamo.getId().equals(idPrestamo))
                .findFirst()
                .orElse(null);
    }

    public void update(Prestamo prestamo) {
        prestamoDAO.update(prestamo); // Actualizar en DB
    }


    public List<Prestamo> getPrestamos() {
        return prestamos;
    }
}
