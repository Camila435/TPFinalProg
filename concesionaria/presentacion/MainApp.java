package concesionaria.presentacion;

import concesionaria.dominio.Auto;
import concesionaria.dominio.Camioneta;
import concesionaria.dominio.DatoInvalidoException;
import concesionaria.dominio.Estado;
import concesionaria.dominio.Motocicleta;
import concesionaria.dominio.PatenteInvalidaException;
import concesionaria.dominio.PuertasInsuficientesException;
import concesionaria.dominio.TipoCarroceria;
import concesionaria.dominio.TipoCarroceriaMoto;
import concesionaria.dominio.Vehiculo;
import concesionaria.negocio.Concesionaria;
import concesionaria.negocio.GestorArchivosVehiculos;
import concesionaria.negocio.TallerGeneral;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Capa de PRESENTACIÓN: menú interactivo por consola para GyQ Concesionaria.
 * Gestiona la experiencia del operador con persistencia automática en disco,
 * validación estricta de datos numéricos y cancelación unificada mediante 'Z'.
 *
 * @author GyQ Concesionaria
 * @version 2.0
 */
public class MainApp {

    private static final Scanner scanner = new Scanner(System.in);
    private static final Concesionaria concesionaria = new Concesionaria();
    private static final GestorArchivosVehiculos gestorArchivos = new GestorArchivosVehiculos();
    private static final TallerGeneral<Vehiculo> tallerGeneral = new TallerGeneral<>();

    private static final String ARCHIVO_MAESTRO = "inventario_maestro.dat";
    private static final String ARCHIVO_CLON = "inventario_backup_clon.dat";
    private static final String ARCHIVO_RECICLADO = "inventario_reciclado_0km.dat";
    private static final String ARCHIVO_NOVEDADES = "inventario_novedades.dat";
    private static final String ARCHIVO_APAREADO = "inventario_apareado.dat";

    public static void main(String[] args) {
        cargarInventarioInicial();

        int opcion = -1;
        do {
            mostrarMenuPrincipal();
            Integer opLeida = leerEntero("Seleccione una opción: ", false);
            opcion = (opLeida != null) ? opLeida : -1;

            switch (opcion) {
                case 1:
                    registrarVehiculoConsola();
                    break;
                case 2:
                    gestionarCatalogoMarcasModelos();
                    break;
                case 3:
                    listarVehiculos("INVENTARIO COMPLETO (ORDENADO POR AÑO)", concesionaria.getInventarioOrdenadoPorAnio());
                    break;
                case 4:
                    listarPorCategoria();
                    break;
                case 5:
                    listarPorEstado();
                    break;
                case 6:
                    buscarPorPatenteConsola();
                    break;
                case 7:
                    venderVehiculoConsola();
                    break;
                case 8:
                    gestionarRespaldosYActualizaciones();
                    break;
                case 9:
                    ingresarVehiculoATallerConsola();
                    break;
                case 0:
                    guardarCambiosAutomaticamente();
                    System.out.println("\nDatos guardados correctamente. Cerrando sistema de GyQ Concesionaria.");
                    break;
                default:
                    System.out.println("Opción inválida. Ingrese un número del menú.");
                    break;
            }
        } while (opcion != 0);

        scanner.close();
    }

    private static void mostrarMenuPrincipal() {
        System.out.println("\n=======================================================");
        System.out.println("          GyQ CONCESIONARIA MULTIMARCA - VENTAS        ");
        System.out.println("=======================================================");
        System.out.printf(" Stock disponible: %d unidades | Recaudado: $%.2f%n",
                concesionaria.cantidadEnStock(), concesionaria.getTotalRecaudadoVentas());
        System.out.println("-------------------------------------------------------");
        System.out.println(" 1. Registrar nuevo vehículo en stock");
        System.out.println(" 2. Catálogo de Marcas y Modelos");
        System.out.println(" 3. Listar todo el inventario (ordenado por año)");
        System.out.println(" 4. Listar vehículos por categoría");
        System.out.println(" 5. Listar vehículos por estado (0 KM / Usado)");
        System.out.println(" 6. Buscar vehículo por patente");
        System.out.println(" 7. Registrar venta de vehículo");
        System.out.println(" 8. Respaldos, reportes 0 KM y actualización de fábrica");
        System.out.println(" 9. Enviar vehículo a Taller General (Lavado y Service)");
        System.out.println(" 0. Guardar y Salir");
        System.out.println("-------------------------------------------------------");
    }

    /**
     * Submenú comercial orientado al operador de la concesionaria.
     * Internamente delega en las operaciones de clonado, reciclado y apareo de {@link GestorArchivosVehiculos}.
     */
    private static void gestionarRespaldosYActualizaciones() {
        System.out.println("\n--- RESPALDOS Y ACTUALIZACIÓN DE INVENTARIO ---");
        System.out.println(" 1. Crear copia de seguridad del stock actual");
        System.out.println(" 2. Generar listado depurado solo con unidades 0 KM");
        System.out.println(" 3. Importar actualización de precios y nuevos ingresos de fábrica");
        System.out.println(" 4. Restaurar inventario desde la última copia de seguridad");
        System.out.println(" Z. Volver al menú principal");

        Integer op = leerEntero("Seleccione una opción: ", true);
        if (op == null) {
            return;
        }

        try {
            switch (op) {
                case 1:
                    guardarCambiosAutomaticamente();
                    int respaldados = gestorArchivos.clonarArchivo(ARCHIVO_MAESTRO, ARCHIVO_CLON);
                    System.out.println("Copia de seguridad generada con éxito (" + respaldados + " vehículos respaldados).");
                    break;
                case 2:
                    guardarCambiosAutomaticamente();
                    int unidades0Km = gestorArchivos.reciclarArchivoPorEstado(ARCHIVO_MAESTRO, ARCHIVO_RECICLADO, Estado.NUEVO);
                    System.out.println("Listado depurado generado: se conservaron " + unidades0Km + " unidades 0 KM.");
                    listarVehiculos("UNIDADES 0 KM EXPORTADAS", gestorArchivos.leerVehiculos(ARCHIVO_RECICLADO));
                    break;
                case 3:
                    guardarCambiosAutomaticamente();
                    List<Vehiculo> loteNovedades = generarLoteNovedadesDemo();
                    gestorArchivos.guardarVehiculos(ARCHIVO_NOVEDADES, loteNovedades);
                    int totalActualizado = gestorArchivos.aparearArchivos(ARCHIVO_MAESTRO, ARCHIVO_NOVEDADES, ARCHIVO_APAREADO);
                    List<Vehiculo> consolidado = gestorArchivos.leerVehiculos(ARCHIVO_APAREADO);
                    concesionaria.reemplazarInventario(consolidado);
                    guardarCambiosAutomaticamente();
                    System.out.println("Inventario actualizado con novedades de fábrica. Total en stock: " + totalActualizado + " unidades.");
                    break;
                case 4:
                    List<Vehiculo> backup = gestorArchivos.leerVehiculos(ARCHIVO_CLON);
                    if (backup.isEmpty()) {
                        System.out.println("No existe ninguna copia de seguridad previa para restaurar.");
                    } else {
                        concesionaria.reemplazarInventario(backup);
                        guardarCambiosAutomaticamente();
                        System.out.println("Inventario restaurado correctamente (" + backup.size() + " unidades).");
                    }
                    break;
                default:
                    System.out.println("Opción inválida.");
                    break;
            }
        } catch (IOException | ClassNotFoundException | DatoInvalidoException e) {
            System.out.println("[ERROR DE SISTEMA]: " + e.getMessage());
        }
    }

    private static List<Vehiculo> generarLoteNovedadesDemo() throws DatoInvalidoException {
        List<Vehiculo> novedades = new ArrayList<>();
        novedades.add(new Auto("Toyota", "Corolla", 2024, "AG123CD", "Blanco", 0, 29500.0, 4, TipoCarroceria.SEDAN));
        novedades.add(new Camioneta("Ford", "Ranger", 2025, "AH555KL", "Gris Plata", 0, 46000.0, TipoCarroceria.PICKUP, 1050.0, true));
        return novedades;
    }

    private static void registrarVehiculoConsola() {
        try {
            System.out.println("\n--- ALTA DE NUEVO VEHÍCULO ---");
            System.out.println("(Puede ingresar 'Z' en cualquier paso para cancelar y volver)");
            System.out.println("\nSeleccione el tipo de vehículo:");
            System.out.println(" 1. Automóvil");
            System.out.println(" 2. Camioneta");
            System.out.println(" 3. Motocicleta");
            System.out.println(" Z. Cancelar");

            Integer tipo = leerEntero("Tipo: ", true);
            if (tipo == null) return;
            if (tipo < 1 || tipo > 3) {
                System.out.println("Tipo inválido.");
                return;
            }

            String[] marcaYModelo = seleccionarOIngresarMarcaModelo();
            if (marcaYModelo == null) return;
            String marca = marcaYModelo[0];
            String modelo = marcaYModelo[1];

            Integer anio = leerEntero("Año (" + Vehiculo.ANIO_MINIMO + "-" + Vehiculo.anioMaximo() + "): ", true);
            if (anio == null) return;

            String patente = leerTexto("Patente (ej: ABC123 o AB123CD): ");
            if (patente == null) return;

            String color = leerTexto("Color: ");
            if (color == null) return;

            Integer km = leerEntero("Kilometraje (0 para unidad 0 KM): ", true);
            if (km == null) return;

            Double precioBase = leerDouble("Precio base de lista ($): ", true);
            if (precioBase == null) return;

            Vehiculo nuevo = null;

            if (tipo == 1) {
                Integer puertas = leerEntero("Cantidad de puertas (1 a 5): ", true);
                if (puertas == null) return;

                Object carroceriaSel = elegirCarroceriaConOpcionNueva("Carrocería de Automóvil", TipoCarroceria.paraAutos());
                if (carroceriaSel == null) return;

                if (carroceriaSel instanceof TipoCarroceria) {
                    nuevo = new Auto(marca, modelo, anio, patente, color, km, precioBase, puertas, (TipoCarroceria) carroceriaSel);
                } else {
                    nuevo = new Auto(marca, modelo, anio, patente, color, km, precioBase, puertas, (String) carroceriaSel);
                }
            } else if (tipo == 2) {
                Object carroceriaSel = elegirCarroceriaConOpcionNueva("Carrocería de Camioneta", TipoCarroceria.paraCamionetas());
                if (carroceriaSel == null) return;

                Double carga = leerDouble("Capacidad de carga (kg): ", true);
                if (carga == null) return;

                String resp4x4 = leerTexto("¿Posee tracción 4x4? (S/N): ");
                if (resp4x4 == null) return;

                boolean es4x4 = resp4x4.equalsIgnoreCase("S");
                if (carroceriaSel instanceof TipoCarroceria) {
                    nuevo = new Camioneta(marca, modelo, anio, patente, color, km, precioBase, (TipoCarroceria) carroceriaSel, carga, es4x4);
                } else {
                    nuevo = new Camioneta(marca, modelo, anio, patente, color, km, precioBase, (String) carroceriaSel, carga, es4x4);
                }
            } else {
                Integer cilindrada = leerEntero("Cilindrada (cc): ", true);
                if (cilindrada == null) return;

                TipoCarroceriaMoto tipoMoto = elegirOpcionEnum("Estilo de Motocicleta", TipoCarroceriaMoto.values());
                if (tipoMoto == null) return;

                nuevo = new Motocicleta(marca, modelo, anio, patente, color, km, precioBase, cilindrada, tipoMoto);
            }

            concesionaria.agregarVehiculo(nuevo);
            guardarCambiosAutomaticamente();
            System.out.println("\nVehículo registrado y guardado en inventario:");
            System.out.println(" -> " + nuevo);

        } catch (PatenteInvalidaException e) {
            System.out.println("[ERROR DE PATENTE]: " + e.getMessage());
        } catch (PuertasInsuficientesException e) {
            System.out.println("[ERROR EN AUTOMÓVIL]: " + e.getMessage());
        } catch (DatoInvalidoException e) {
            System.out.println("[DATO INVÁLIDO]: " + e.getMessage());
        }
    }

    /**
     * Presenta las opciones del enum {@link TipoCarroceria} y añade una opción final
     * para ingresar una carrocería personalizada si no figura en la lista.
     */
    private static Object elegirCarroceriaConOpcionNueva(String titulo, TipoCarroceria[] opciones) {
        while (true) {
            System.out.println("\n" + titulo + ":");
            for (int i = 0; i < opciones.length; i++) {
                System.out.printf(" %d. %s%n", (i + 1), opciones[i]);
            }
            int opcionOtra = opciones.length + 1;
            System.out.println(" " + opcionOtra + ". Otra (Ingresar nueva carrocería)");
            System.out.println(" Z. Cancelar");

            Integer sel = leerEntero("Seleccione opción: ", true);
            if (sel == null) {
                return null;
            }
            if (sel >= 1 && sel <= opciones.length) {
                return opciones[sel - 1];
            }
            if (sel == opcionOtra) {
                return leerTexto("Ingrese el nombre de la nueva carrocería: ");
            }
            System.out.println("Opción fuera de rango. Intente nuevamente.");
        }
    }

    private static String[] seleccionarOIngresarMarcaModelo() {
        List<String> marcas = concesionaria.getMarcasDisponibles();
        System.out.println("\nSeleccione una Marca del catálogo:");
        for (int i = 0; i < marcas.size(); i++) {
            System.out.printf(" %d. %s%n", (i + 1), marcas.get(i));
        }
        System.out.println(" " + (marcas.size() + 1) + ". Agregar nueva marca");
        System.out.println(" Z. Cancelar");

        Integer opMarca = leerEntero("Opción de marca: ", true);
        if (opMarca == null) return null;

        String marcaElegida;
        if (opMarca >= 1 && opMarca <= marcas.size()) {
            marcaElegida = marcas.get(opMarca - 1);
        } else if (opMarca == marcas.size() + 1) {
            marcaElegida = leerTexto("Nombre de la nueva Marca: ");
            if (marcaElegida == null) return null;
        } else {
            System.out.println("Opción inválida.");
            return null;
        }

        List<String> modelos = concesionaria.getModelosPorMarca(marcaElegida);
        String modeloElegido;
        if (!modelos.isEmpty()) {
            System.out.println("\nModelos disponibles para " + marcaElegida + ":");
            for (int i = 0; i < modelos.size(); i++) {
                System.out.printf(" %d. %s%n", (i + 1), modelos.get(i));
            }
            System.out.println(" " + (modelos.size() + 1) + ". Agregar nuevo modelo");
            System.out.println(" Z. Cancelar");

            Integer opModelo = leerEntero("Opción de modelo: ", true);
            if (opModelo == null) return null;

            if (opModelo >= 1 && opModelo <= modelos.size()) {
                modeloElegido = modelos.get(opModelo - 1);
            } else if (opModelo == modelos.size() + 1) {
                modeloElegido = leerTexto("Nombre del nuevo Modelo: ");
                if (modeloElegido == null) return null;
            } else {
                System.out.println("Opción inválida.");
                return null;
            }
        } else {
            modeloElegido = leerTexto("Modelo para " + marcaElegida + ": ");
            if (modeloElegido == null) return null;
        }

        return new String[] { marcaElegida, modeloElegido };
    }

    private static void gestionarCatalogoMarcasModelos() {
        System.out.println("\n--- CATÁLOGO ACTUAL DE MARCAS Y MODELOS ---");
        for (Map.Entry<String, List<String>> entrada : concesionaria.getCatalogoMarcas().entrySet()) {
            System.out.println(" * " + entrada.getKey() + ": " + entrada.getValue());
        }

        System.out.println("\n 1. Agregar nueva Marca o Modelo");
        System.out.println(" Z. Volver al menú principal");
        Integer op = leerEntero("Seleccione opción: ", true);
        if (op == null) return;

        if (op == 1) {
            String marca = leerTexto("Nombre de la Marca (existente o nueva): ");
            if (marca == null) return;
            String modelo = leerTexto("Nombre del Modelo nuevo: ");
            if (modelo == null) return;

            concesionaria.registrarMarcaYModelo(marca, modelo);
            System.out.println("Catálogo actualizado: " + marca + " -> " + modelo);
        }
    }

    private static void listarPorCategoria() {
        System.out.println("\n--- FILTRAR POR CATEGORÍA ---");
        System.out.println(" 1. Automóviles");
        System.out.println(" 2. Camionetas");
        System.out.println(" 3. Motocicletas");
        System.out.println(" Z. Volver al menú principal");
        Integer op = leerEntero("Seleccione categoría: ", true);
        if (op == null) return;

        switch (op) {
            case 1:
                listarVehiculos("LISTADO DE AUTOMÓVILES", concesionaria.getAutos());
                break;
            case 2:
                listarVehiculos("LISTADO DE CAMIONETAS", concesionaria.getCamionetas());
                break;
            case 3:
                listarVehiculos("LISTADO DE MOTOCICLETAS", concesionaria.getMotos());
                break;
            default:
                System.out.println("Categoría inválida.");
                break;
        }
    }

    private static void listarPorEstado() {
        System.out.println("\n--- FILTRAR POR ESTADO COMERCIAL ---");
        System.out.println(" 1. Nuevos (0 KM)");
        System.out.println(" 2. Usados");
        System.out.println(" Z. Volver al menú principal");
        Integer op = leerEntero("Seleccione estado: ", true);
        if (op == null) return;

        if (op == 1) {
            listarVehiculos("VEHÍCULOS NUEVOS (0 KM)", concesionaria.getPorEstado(Estado.NUEVO));
        } else if (op == 2) {
            listarVehiculos("VEHÍCULOS USADOS", concesionaria.getPorEstado(Estado.USADO));
        } else {
            System.out.println("Estado inválido.");
        }
    }

    private static void listarVehiculos(String titulo, List<? extends Vehiculo> lista) {
        System.out.println("\n--- " + titulo + " (" + lista.size() + ") ---");
        if (lista.isEmpty()) {
            System.out.println("No hay vehículos que coincidan con el criterio.");
            return;
        }
        for (Vehiculo v : lista) {
            System.out.println(" * " + v);
        }
    }

    private static void buscarPorPatenteConsola() {
        String patente = leerTexto("\nIngrese la patente a buscar (o 'Z' para volver): ");
        if (patente == null) return;

        Vehiculo encontrado = concesionaria.buscarPorPatente(patente);
        if (encontrado != null) {
            System.out.println("Vehículo encontrado: " + encontrado);
        } else {
            System.out.println("No existe ningún vehículo en stock con la patente: " + patente);
        }
    }

    private static void venderVehiculoConsola() {
        if (concesionaria.estaVacio()) {
            System.out.println("\nNo hay vehículos en stock para vender.");
            return;
        }
        try {
            String patente = leerTexto("\nIngrese la patente del vehículo a vender (o 'Z' para volver): ");
            if (patente == null) return;

            Vehiculo v = concesionaria.buscarPorPatente(patente);
            if (v != null) {
                System.out.println("Unidad seleccionada: " + v);
                String confirmacion = leerTexto("¿Confirmar venta? (S/N): ");
                if (confirmacion == null) return;

                if (confirmacion.equalsIgnoreCase("S")) {
                    double precioFinal = concesionaria.venderVehiculo(patente);
                    guardarCambiosAutomaticamente();
                    System.out.printf("¡VENTA CONCRETADA! Precio final cobrado: $%.2f%n", precioFinal);
                } else {
                    System.out.println("Venta cancelada.");
                }
            } else {
                System.out.println("No se encontró el vehículo con patente: " + patente);
            }
        } catch (DatoInvalidoException e) {
            System.out.println("[ERROR EN VENTA]: " + e.getMessage());
        }
    }

    private static void ingresarVehiculoATallerConsola() {
        String patente = leerTexto("\nIngrese la patente del vehículo a ingresar al taller (o 'Z' para volver): ");
        if (patente == null) return;

        Vehiculo encontrado = concesionaria.buscarPorPatente(patente);
        if (encontrado != null) {
            tallerGeneral.procesarVehiculo(encontrado);
        } else {
            System.out.println("No existe ningún vehículo en stock con la patente: " + patente);
        }
    }

    private static <T> T elegirOpcionEnum(String titulo, T[] opciones) {
        while (true) {
            System.out.println("\n" + titulo + ":");
            for (int i = 0; i < opciones.length; i++) {
                System.out.printf(" %d. %s%n", (i + 1), opciones[i]);
            }
            System.out.println(" Z. Cancelar");
            Integer sel = leerEntero("Seleccione opción: ", true);
            if (sel == null) {
                return null;
            }
            if (sel >= 1 && sel <= opciones.length) {
                return opciones[sel - 1];
            }
            System.out.println("Opción fuera de rango. Intente nuevamente.");
        }
    }

    /**
     * Lee exclusivamente un número entero. Si se habilita la cancelación y el operador
     * ingresa 'Z' o 'z', devuelve {@code null}. Rechaza cualquier otra letra o símbolo.
     */
    private static Integer leerEntero(String mensaje, boolean permitirCancelarConZ) {
        while (true) {
            System.out.print(mensaje);
            String entrada = scanner.nextLine().trim();
            if (permitirCancelarConZ && entrada.equalsIgnoreCase("Z")) {
                return null;
            }
            try {
                return Integer.parseInt(entrada);
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Debe ingresar únicamente números enteros"
                        + (permitirCancelarConZ ? " (o 'Z' para volver)." : "."));
            }
        }
    }

    /**
     * Lee exclusivamente un número decimal. Acepta 'Z' o 'z' para cancelar.
     * Rechaza letras o caracteres no numéricos.
     */
    private static Double leerDouble(String mensaje, boolean permitirCancelarConZ) {
        while (true) {
            System.out.print(mensaje);
            String entrada = scanner.nextLine().trim().replace(",", ".");
            if (permitirCancelarConZ && entrada.equalsIgnoreCase("Z")) {
                return null;
            }
            try {
                return Double.parseDouble(entrada);
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Debe ingresar un importe numérico válido"
                        + (permitirCancelarConZ ? " (o 'Z' para volver)." : "."));
            }
        }
    }

    /**
     * Lee una cadena de texto no vacía. Si el operador ingresa 'Z' o 'z', devuelve {@code null}.
     */
    private static String leerTexto(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String texto = scanner.nextLine().trim();
            if (texto.equalsIgnoreCase("Z")) {
                return null;
            }
            if (!texto.isEmpty()) {
                return texto;
            }
            System.out.println("Este campo no puede quedar vacío (o ingrese 'Z' para volver).");
        }
    }

    /**
     * Persiste automáticamente el estado actual del inventario en disco.
     */
    private static void guardarCambiosAutomaticamente() {
        try {
            gestorArchivos.guardarVehiculos(ARCHIVO_MAESTRO, concesionaria.getTodos());
        } catch (IOException e) {
            System.err.println("[ADVERTENCIA]: No se pudo guardar automáticamente el inventario: " + e.getMessage());
        }
    }

    /**
     * Recupera el inventario desde disco al iniciar; si es la primera ejecución,
     * carga el lote inicial y lo guarda automáticamente.
     */
    private static void cargarInventarioInicial() {
        try {
            List<Vehiculo> desdeArchivo = gestorArchivos.leerVehiculos(ARCHIVO_MAESTRO);
            if (!desdeArchivo.isEmpty()) {
                concesionaria.reemplazarInventario(desdeArchivo);
                return;
            }

            concesionaria.agregarVehiculo(new Auto("Toyota", "Corolla", 2024, "AG123CD", "Blanco", 0, 28000.0, 4, TipoCarroceria.SEDAN));
            concesionaria.agregarVehiculo(new Auto("Ford", "Focus", 2019, "AD456EF", "Gris", 62000, 16500.0, 5, TipoCarroceria.HATCHBACK));
            concesionaria.agregarVehiculo(new Camioneta("Toyota", "Hilux", 2023, "AF789GH", "Negro", 15000, 42000.0, TipoCarroceria.PICKUP, 1000.0, true));
            concesionaria.agregarVehiculo(new Camioneta("Volkswagen", "Amarok", 2025, "AG999ZZ", "Azul", 0, 48000.0, TipoCarroceria.PICKUP, 1100.0, true));
            concesionaria.agregarVehiculo(new Motocicleta("Honda", "CB 250 Twister", 2024, "A123BCD", "Rojo", 0, 5200.0, 250, TipoCarroceriaMoto.NAKED));
            concesionaria.agregarVehiculo(new Motocicleta("Yamaha", "XTZ 125", 2021, "A098XYZ", "Blanco", 18500, 3100.0, 125, TipoCarroceriaMoto.ENDURO));

            guardarCambiosAutomaticamente();
        } catch (IOException | ClassNotFoundException | DatoInvalidoException e) {
            System.err.println("Error al inicializar inventario: " + e.getMessage());
        }
    }
}