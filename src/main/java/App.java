import controlador.ConsoleMenu;
import modelo.Usuario;

import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        // Admin email: admin@gmail.com
        // Admin password: admin

        ConsoleMenu.initStartMenu(sc);
        sc.close();  // Cerrar scanner para liberar recursos
    }
}
