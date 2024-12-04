package controlador;

import modelo.Ejemplar;
import modelo.Libro;
import modelo.Prestamo;
import modelo.Usuario;
import repositorio.GenericDAO;
import servicio.EjemplarService;
import servicio.LibroService;
import servicio.PrestamoService;
import servicio.UsuarioService;

import java.util.List;
import java.util.Scanner;

public class ConsoleMenu {

    private static final GenericDAO<Usuario> usuarioDAO = new GenericDAO<>(Usuario.class);
    private static final GenericDAO<Libro> libroDAO = new GenericDAO<>(Libro.class);
    private static final GenericDAO<Ejemplar> ejemplarDAO = new GenericDAO<>(Ejemplar.class);
    private static final GenericDAO<Prestamo> prestamoDAO = new GenericDAO<>(Prestamo.class);

    private static final UsuarioService usuarioService = new UsuarioService(usuarioDAO);
    private static final LibroService libroService = new LibroService(libroDAO);
    private static final EjemplarService ejemplarService = new EjemplarService(ejemplarDAO, libroService);
    private static final PrestamoService prestamoService = new PrestamoService(prestamoDAO, usuarioService, ejemplarService);

    public static void initStartMenu(Scanner sc) {
        int option;
        do {
            System.out.println("--------------------------------");
            System.out.println("1 - Iniciar sesión");
            System.out.println("2 - Registrarse");
            System.out.println("0 - Salir");
            System.out.println("--------------------------------");

            option = sc.nextInt();
            sc.nextLine(); // Limpiar buffer

            switch (option) {
                case 1:
                    initLogin(sc); // Iniciar login
                    break;
                case 2:
                    initSignUp(sc); // Iniciar registro
                    break;
                case 0:
                    System.out.println("Saliendo...");
                    break;
                default:
                    System.out.println("ERROR. Intenta de nuevo.");
            }
        } while (option != 0);
    }

    private static void initLogin(Scanner sc) {
        String email, password;
        System.out.println("--------------------------------");
        System.out.println("Iniciar sesión");
        System.out.println("--------------------------------");

        System.out.println("Email: ");
        email = sc.nextLine();

        System.out.println("Contraseña: ");
        password = sc.nextLine();

        try {
            // Intentar iniciar sesión con los datos ingresados
            // Obtener usuario con sesión activa
            Usuario usuarioActivo = usuarioService.iniciarSesion(email, password); 
            
            // Mostrar menú de operaciones para el usuario activo dependiendo de su tipo
            initOperationsMenu(sc, usuarioActivo);
            
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static void initSignUp(Scanner sc) {
        String dni, nombre, email, password;
        System.out.println("--------------------------------");
        System.out.println("Registrarse");
        System.out.println("--------------------------------");

        System.out.println("DNI: ");
        dni = sc.nextLine();

        System.out.println("Nombre: ");
        nombre = sc.nextLine();

        System.out.println("Email: ");
        email = sc.nextLine();

        System.out.println("Contraseña: ");
        password = sc.nextLine();

        try {
            // Intentar iniciar sesión con los datos ingresados
            // Obtener usuario con sesión activa
            Usuario usuarioActivo = usuarioService.registrar(dni, nombre, email, password, null);
            initOperationsMenu(sc, usuarioActivo);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    
    // Iniciar menú de operaciones según el tipo de usuario
    private static void initOperationsMenu(Scanner sc, Usuario usuarioActivo) {
        // Posibles implementacioneso:
        //      Operaciones de usuarios (admin/user) auditadas.
        //      Solicitud de préstamos desde el menú de usuario común
        if (usuarioActivo.getTipo().equals("administrador"))
            initAdminMenu(sc, usuarioActivo);
        else 
            initUserMenu(sc, usuarioActivo);
    }

    private static void initAdminMenu(Scanner sc, Usuario usuarioActivo) {
        int option;
        do {
            System.out.println("--------------------------------");
            System.out.println("1 - Registrar libro");
            System.out.println("2 - Registrar ejemplar");
            System.out.println("3 - Crear prestamo");
            System.out.println("4 - Devolver prestamo");
            System.out.println("5 - Listar libros y stock");
            System.out.println("6 - Listar préstamos");
            System.out.println("0 - Salir");
            System.out.println("--------------------------------");

            option = sc.nextInt();
            sc.nextLine(); // Limpiar buffer

            switch (option) {
                case 1:
                    registrarLibro(sc);
                    break;
                case 2:
                    registrarEjemplar(sc);
                    break;
                case 3:
                    crearPrestamo(sc);
                    break;
                case 4:
                    devolverPrestamo(sc);
                    break;
                case 5:
                    listarLibrosYStock();
                    break;
                case 6:
                    listarPrestamos();
                    break;
                case 0:
                    System.out.println("Saliendo...");
                    break;
                default:
                    System.out.println("ERROR. Intenta de nuevo.");
            }
        } while (option != 0);
    }

    private static void listarPrestamos() {
        List<Prestamo> prestamos = prestamoService.getPrestamos();
        System.out.println("--------------------------------");
        System.out.println("Listado de Prestamos");
        System.out.println("--------------------------------");

        prestamos.forEach(System.out::println);
    }

    private static void listarLibrosYStock() {
        List<Libro> libros = libroService.getLibros();
        System.out.println("--------------------------------");
        System.out.println("Listado de Libros y Stock");
        System.out.println("--------------------------------");

        for (Libro libro : libros) {
            int stock = libroService.getStockLibro(libro);
            System.out.println(libro + " - Stock: " + stock);
        }
    }

    private static void devolverPrestamo(Scanner sc) {
        int idPrestamo;
        System.out.println("--------------------------------");
        System.out.println("Devolver Prestamo");
        System.out.println("--------------------------------");

        System.out.println("ID Prestamo: ");
        idPrestamo = sc.nextInt();
        sc.nextLine(); // Limpiar buffer

        try {
            // Devolver prestamo
            prestamoService.devolver(idPrestamo);
            System.out.println("Prestamo devuelto correctamente.");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static void crearPrestamo(Scanner sc) {
        int idUsuario, idEjemplar;
        System.out.println("--------------------------------");
        System.out.println("Crear Prestamo");
        System.out.println("--------------------------------");

        System.out.println("ID Usuario: ");
        idUsuario = sc.nextInt();
        sc.nextLine(); // Limpiar buffer

        System.out.println("ID Ejemplar: ");
        idEjemplar = sc.nextInt();
        sc.nextLine(); // Limpiar buffer

        try {
            // Crear prestamo con los datos ingresados
            prestamoService.registrar(idUsuario, idEjemplar);
            System.out.println("Prestamo registrado correctamente.");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static void registrarEjemplar(Scanner sc) {
        String isbn13, estado;
        System.out.println("--------------------------------");
        System.out.println("Registrar Ejemplar");
        System.out.println("--------------------------------");

        System.out.println("ISBN13: ");
        isbn13 = sc.nextLine();

        System.out.println("Estado (Dañado, Disponible) (ENTER = DISPONIBLE) : ");
        estado = sc.nextLine();

        try {
            // Registrar ejemplar con los datos ingresados
            ejemplarService.registrar(isbn13, estado);
            System.out.println("Ejemplar registrado correctamente.");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static void registrarLibro(Scanner sc) {
        String isbn13, titulo, autor;
        System.out.println("--------------------------------");
        System.out.println("Registrar Libro");
        System.out.println("--------------------------------");

        System.out.println("ISBN13: ");
        isbn13 = sc.nextLine();

        System.out.println("Título: ");
        titulo = sc.nextLine();

        System.out.println("Autor: ");
        autor = sc.nextLine();

        try {
            // Registrar libro con los datos ingresados
            libroService.registrar(isbn13, titulo, autor);
            System.out.println("Libro registrado correctamente.");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static void initUserMenu(Scanner sc, Usuario usuarioActivo) {
        showPrestamosUsuario(sc, usuarioActivo);
    }

    private static void showPrestamosUsuario(Scanner sc, Usuario usuarioActivo) {
        System.out.println("--------------------------------");
        System.out.printf("Hola %s, tienes %d prestamos activos:\n", usuarioActivo.getNombre(), usuarioActivo.getNumeroPrestamosActivos());
        System.out.println("--------------------------------");
        usuarioActivo.getPrestamos().forEach(System.out::println);
    }

}
