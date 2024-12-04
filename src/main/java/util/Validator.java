package util;

import modelo.Ejemplar;
import modelo.Prestamo;
import modelo.Usuario;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class Validator {

    // Validación campos requeridos
    public static boolean isNotFilled(Object ... objects) {
        return Arrays.stream(objects).anyMatch(o -> o == null || o.toString().isBlank());
    }

    // Validación Documento Identidad
    public static boolean isDocumentValid (String di) {
        // Verificar que el DI tiene 9 caracteres
        if (di.length() != 9)
            return false;

        return isControlDigitCorrect(di);
    }

    private static boolean isControlDigitCorrect(String di) {
        List<Character> letras = List.of(
                'T', 'R', 'W', 'A', 'G', 'M', 'Y', 'F', 'P', 'D', 'X', 'B',
                'N', 'J', 'Z', 'S', 'Q', 'V', 'H', 'L', 'C', 'K', 'E'
        );

        int diDigits;

        // Verificar si es extranjero o español y almacenar los dígitos
        char letraInicio = di.charAt(0);

        // Extranjero
        if (Character.isLetter(letraInicio)) {
            diDigits = Integer.parseInt(getNumeroEquivalenteNIF(letraInicio) + di.substring(1,8));
        }
        // Español
        else
            diDigits = Integer.parseInt(di.substring(0, 8));

        // Calcular el resto de la división del número por 23
        int resto = diDigits % 23;

        // Obtener la letra introducida
        char letraIntroducida = di.charAt(di.length()-1);

        // Obtener la letra esperada de la lista
        char letraEsperada = letras.get(resto);

        // Comparar la letra introducida con la letra esperada
        return letraIntroducida == letraEsperada;
    }

    public static int getNumeroEquivalenteNIF(char letraInicio) {
        return switch (letraInicio) {
            case 'X' -> 0;
            case 'Y' -> 1;
            case 'Z' -> 2;
            default -> throw new IllegalArgumentException("Caracter no válido para NIF");
        };
    }

    // Validación Correo Electrónico
    public static boolean isEmailValid(String email) {
        // Validación básica del correo electrónico
        String regex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
        if (!email.matches(regex))
            return false;

        // Verifica si el dominio es válido
        Set<String> allowedDomains = Set.of(
                "gmail.com", "outlook.com", "yahoo.com", "hotmail.com", "icloud.com"
        );
        String domain = email.substring(email.indexOf('@') + 1);

        // Retorna true si el dominio está en el Set de dominios permitidos,
        // false en caso contrario
        return allowedDomains.contains(domain);
    }

    // Validación Tipo de Usuario
    public static boolean isTipoValid(String tipo) {
        // Retorna true si el tipo es "administrador" o "normal",
        // false en caso contrario
        return tipo.equalsIgnoreCase("administrador")
                || tipo.equalsIgnoreCase("normal");
    }

    // Validación Contraseña
    public static boolean isPasswordCorrectForThisUser(Usuario usuario, String password) {
        // Retorna true si la contraseña es correcta para el usuario,
        // false en caso contrario
        return usuario.getPassword().equals(password);
    }

    // Validación ISBN13
    public static boolean isIsbn13Valid(String isbn13) {
        // Validar entrada
        String regex = "^[0-9]{13}$";
        if (!isbn13.matches(regex))
            return false;

        int sum = 0;
        // Recorrer los 12 primeros dígitos del ISBN13
        for (int i = 0; i < 12; i++) {
            int digit = Character.getNumericValue(isbn13.charAt(i)); // Obtener dígito en cada iteración
            sum += digit * (i % 2 == 0? 1 : 3); // Sumar dígito multiplicado por 1 o 3 según la posición
        }

        int digitoControlEsperado = (10 - (sum % 10)) % 10; // Calcular dígito de control esperado (0-9)
        int digitoControlEntrada = Character.getNumericValue(isbn13.charAt(12)); // Obtener dígito de control introducido

        // Retornar true si el dígito de control es correcto,
        // false en caso contrario
        return digitoControlEsperado == digitoControlEntrada;
    }

    // Validación Estado
    public static boolean isEstadoValid(String estado) {
        // Retorna true si el estado es "Disponible", "Prestado" o "Dañado",
        // false en caso contrario
        return estado.equalsIgnoreCase("disponible")
                || estado.equalsIgnoreCase("prestado")
                || estado.equalsIgnoreCase("dañado");
    }

    // Validación Ejemplar disponible
    public static boolean isEjemplarDisponible(Ejemplar ejemplarRef) {
        // Retorna true si el ejemplar está disponible,
        // false en caso contrario
        return ejemplarRef.getEstado().equalsIgnoreCase("disponible");
    }

    // Validaciones del Usuario para el préstamo
    public static boolean isUsuarioPenalizado(Usuario usuarioRef) {
        // Retorna true si el usuario está penalizado,
        // false en caso contrario
        return usuarioRef.getPenalizacionHasta()!= null
                && LocalDate.now().isBefore(usuarioRef.getPenalizacionHasta());
    }

    public static boolean hasUsuarioLimitePrestamosActivos(Usuario usuarioRef) {
        // Retorna true si el usuario tiene más de 3 préstamos activos,
        // false en caso contrario
        return usuarioRef.getNumeroPrestamosActivos() > 3;
    }

    // Validaciones del préstamo para la devolución
    public static boolean isPrestamoDevuelto(Prestamo prestamoToDevolver) {
        // Retorna true si el préstamo ha sido devuelto,
        // false en caso contrario
        return prestamoToDevolver.getFechaDevolucion() == null;
    }

    public static boolean isPrestamoDevueltoEnPlazo(Prestamo prestamoToDevolver) {
        LocalDate fechaDevolucionMaxima = prestamoToDevolver.getFechaInicio().plusDays(15); // Fecha máxima de devolución
        if (!isPrestamoDevuelto(prestamoToDevolver)) // Valida si el préstamo ha sido devuelto
                throw new IllegalArgumentException("El préstamo no ha sido devuelto");
        // Retorna true si la fecha de devolución es antes de la fecha máxima,
        // false en caso contrario
        return prestamoToDevolver.getFechaDevolucion().isBefore(fechaDevolucionMaxima);
    }
}
