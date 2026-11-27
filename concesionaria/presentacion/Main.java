package concesionaria.presentacion;

/**
 * Punto de entrada de la aplicación GyQ Concesionaria (Capa de Presentación).
 * Orquesta el ciclo de ejecución del menú principal y delega cada caso de uso
 * en {@link MenuConsola}.
 *
 * @author Camila Quintero
 * @author Guadalupe Gallardo
 * @version 3.1
 */
public class Main {

    /**
     * Método principal que inicia el sistema de gestión de la concesionaria.
     *
     * @param args Argumentos de línea de comandos (no utilizados).
     */
    public static void main(String[] args) {
        MenuConsola menu = new MenuConsola();
        menu.cargarInventario();

        int opcion = -1;
        do {
            menu.mostrarMenuPrincipal();
            opcion = menu.leerOpcionPrincipal();

            switch (opcion) {
                case 1:
                    menu.registrarVehiculo();
                    break;
                case 2:
                    menu.gestionarCatalogo();
                    break;
                case 3:
                    menu.listarInventarioCompleto();
                    break;
                case 4:
                    menu.listarPorCategoria();
                    break;
                case 5:
                    menu.listarPorEstado();
                    break;
                case 6:
                    menu.buscarPorPatente();
                    break;
                case 7:
                    menu.venderVehiculo();
                    break;
                case 8:
                    menu.modificarVehiculo();
                    break;
                case 9:
                    menu.enviarATaller();
                    break;
                case 0:
                    menu.guardarYSalir();
                    break;
                default:
                    System.out.println("Opción inválida. Ingrese un número del 0 al 9.");
                    break;
            }
        } while (opcion != 0);
    }
}