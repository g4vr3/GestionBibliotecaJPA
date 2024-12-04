package servicio;

import modelo.Usuario;
import repositorio.GenericDAO;
import util.Validator;

import java.util.List;

public class UsuarioService {
    private final GenericDAO<Usuario> usuarioDAO;
    private final List<Usuario> usuarios;

    public UsuarioService(GenericDAO<Usuario> usuarioDAO) {
        this.usuarioDAO = usuarioDAO;
        this.usuarios = usuarioDAO.readAll();
    }

    public Usuario registrar(String dni, String nombre, String email, String password, String tipo) {

        // Validación de campos requeridos
        if (Validator.isNotFilled(dni, nombre, email, password))
            throw new IllegalArgumentException("Los campos DNI, nombre, email y contraseña son obligatorios");

        // Usuario de tipo común si no se especifica
        // Pensado para que un usuario común no vea la opción de registrarse como administrador
        // Los administradores se registraran desde el programa y se le aportarán las credenciales de acceso
        // Posible implementación de un panel master para registrar usuarios administradores
        if (Validator.isNotFilled(tipo))
            tipo = "normal";

        // Validación de duplicidad de dni o nombre
        Usuario usuarioExistente = readByEmail(email);
        if (usuarioExistente != null)
            throw new IllegalArgumentException("Este email ya está en uso");
        usuarioExistente = readByDni(dni);
        if (usuarioExistente != null)
            throw new IllegalArgumentException("Este DNI ya está en uso");

        // Validación de campos válidos
        if (!Validator.isDocumentValid(dni))
            throw new IllegalArgumentException("DNI no válido");
        if (!Validator.isEmailValid(email))
            throw new IllegalArgumentException("Email no válido");
        if (!Validator.isTipoValid(tipo))
            throw new IllegalArgumentException("Tipo de usuario no válido");

        Usuario usuarioToCreate = new Usuario(dni, nombre, email, password, tipo);
        usuarioDAO.create(usuarioToCreate); // Crear en DB
        usuarios.add(usuarioToCreate); // Añadir a la lista de memoria

        // Retornar usuario con sesión activa
        return usuarioToCreate;
    }

    public Usuario iniciarSesion(String email, String password) {

        // Validación de campos requeridos
        if (Validator.isNotFilled(email, password))
            throw new IllegalArgumentException("Todos los campos son obligatorios");

        // Validación de credenciales
        Usuario usuarioToLog = readByEmail(email);
        if (usuarioToLog == null)
            throw new IllegalArgumentException("El email es incorrecto");
        if (!Validator.isPasswordCorrectForThisUser(usuarioToLog, password))
            throw new IllegalArgumentException("La contraseña es incorrecta");

        // Devolver usuario con sesión activa
        return usuarios.stream()
               .filter(u -> u.getEmail().equalsIgnoreCase(email))
               .findFirst()
               .orElse(null);
    }

    // Retorna usuario por ID
    public Usuario read(int idUsuario) {
        return usuarios.stream()
                .filter(u -> u.getId().equals(idUsuario))
                .findFirst()
                .orElse(null);
    }

    // Retorna usuario por email
    private Usuario readByEmail(String email) {
        return usuarios.stream()
               .filter(u -> u.getEmail().equalsIgnoreCase(email))
               .findFirst()
               .orElse(null);
    }

    // Retorna usuario por DNI
    private Usuario readByDni(String dni) {
        return usuarios.stream()
               .filter(u -> u.getDni().equals(dni))
               .findFirst()
               .orElse(null);
    }

    public void penalizarUsuario(Usuario usuario) {
        usuario.setPenalizacionHasta(usuario.getPenalizacionHasta().plusDays(15));  // Añade 15 días a la penalización
    }
}
