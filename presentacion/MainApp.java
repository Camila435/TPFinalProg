package presentacion;

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
import negocio.Concesionaria;
import negocio.GestorArchivosVehiculos;
import negocio.TallerGeneral;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Capa de PRESENTACIÓN: menú interactivo por consola para GyQ Concesionaria.
 * Permite volver al menú principal desde cualquier submenú u operación ingresando 0,
 * e integra la gestión de archivos serializados (guardado, carga, clonado, reciclado y apareo).
 *
 * @author GyQ Concesionaria
 * @version 1.1
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

    /**
     * Punto de entrada principal de la aplicación de consola.
     *
     * @param args Argumentos de línea de comandos (no utilizados).
     */
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
                    gestionarArchivosBinarios();
                    break;
                case 9:
                    ingresarVehiculoATallerConsola();
                    break;
                case 0:
                    System.out.println("Cerrando sistema de GyQ Concesionaria. ¡Hasta pronto!");
                    break;
                default:
                    System.out.println("Opción inválida. Intente nuevamente.");
                    break;
            }
        } while (opcion != 0);

        scanner.close();
    }

    /**
     * Imprime en consola las opciones del menú principal y el resumen de stock.
     */
    private static void mostrarMenuPrincipal() {
        System.out.println("\n=======================================================");
        System.out.println("          GyQ CONCESIONARIA MULTIMARCA - VENTAS        ");
        System.out.println("=======================================================");
        System.out.printf(" Stock disponible: %d unidades | Recaudado: $%.2f%n",
                concesionaria.cantidadEnStock(), concesionaria.getTotalRecaudadoVentas());
        System.out.println("-------------------------------------------------------");
        System.out.println(" 1. Registrar nuevo vehículo en stock (Alta)");
        System.out.println(" 2. Ver o agregar nuevas Marcas y Modelos al catálogo");
        System.out.println(" 3. Listar todo el inventario (ordenado por año)");
        System.out.println(" 4. Listar vehículos por categoría (Auto / Camioneta / Moto)");
        System.out.println(" 5. Listar vehículos por estado (0 KM / Usado)");
        System.out.println(" 6. Buscar vehículo por patente");
        System.out.println(" 7. Registrar venta de vehículo (Baja por venta)");
        System.out.println(" 8. Archivos binarios (Guardar, Leer, Clonar, Reciclar, Aparear)");
        System.out.println(" 9. Enviar vehículo a Taller General (Lavado y Mantenimiento)");
        System.out.println(" 0. Salir");
        System.out.println("-------------------------------------------------------");
    }

    /**
     * Envía un vehículo del stock al {@link TallerGeneral} para realizar
     * lavado obligatorio y mantenimiento preventivo si es usado.
     */
    private static void ingresarVehiculoATallerConsola() {
        String patente = leerTextoConCancelacion("\nIngrese la patente del vehículo a ingresar al taller [0 para volver]: ");
        if (patente == null) {
            System.out.println("Volviendo al menú principal...");
            return;
        }
        Vehiculo encontrado = concesionaria.buscarPorPatente(patente);
        if (encontrado != null) {
            tallerGeneral.procesarVehiculo(encontrado);
        } else {
            System.out.println("No existe ningún vehículo en stock con la patente: " + patente);
        }
    }

    /**
     * Submenú para operar sobre archivos binarios serializados mediante {@link GestorArchivosVehiculos}.
     */
    private static void gestionarArchivosBinarios() {
        System.out.println("\n--- PERSISTENCIA Y PROCESAMIENTO DE ARCHIVOS BINARIOS ---");
        System.out.println(" 1. Guardar inventario actual en archivo maestro (" + ARCHIVO_MAESTRO + ")");
        System.out.println(" 2. Cargar inventario desde archivo maestro (" + ARCHIVO_MAESTRO + ")");
        System.out.println(" 3. Clonar archivo maestro a respaldo (" + ARCHIVO_CLON + ")");
        System.out.println(" 4. Reciclar archivo maestro (filtrar solo unidades 0 KM a " + ARCHIVO_RECICLADO + ")");
        System.out.println(" 5. Aparear archivo maestro con novedades (" + ARCHIVO_APAREADO + ")");
        System.out.println(" 0. Volver al menú principal");

        Integer op = leerEntero("Seleccione operación de archivo: ", true);
        if (op == null) {
            System.out.println("Volviendo al menú principal...");
            return;
        }

        try {
            switch (op) {
                case 1:
                    gestorArchivos.guardarVehiculos(ARCHIVO_MAESTRO, concesionaria.getTodos());
                    System.out.println("Inventario serializado correctamente en '" + ARCHIVO_MAESTRO + "' ("
                            + concesionaria.cantidadEnStock() + " unidades).");
                    break;
                case 2:
                    List<Vehiculo> recuperados = gestorArchivos.leerVehiculos(ARCHIVO_MAESTRO);
                    if (recuperados.isEmpty()) {
                        System.out.println("El archivo '" + ARCHIVO_MAESTRO + "' está vacío o recién fue creado.");
                    } else {
                        concesionaria.reemplazarInventario(recuperados);
                        System.out.println("Inventario restaurado desde archivo (" + recuperados.size() + " unidades).");
                    }
                    break;
                case 3:
                    gestorArchivos.guardarVehiculos(ARCHIVO_MAESTRO, concesionaria.getTodos());
                    int clonados = gestorArchivos.clonarArchivo(ARCHIVO_MAESTRO, ARCHIVO_CLON);
                    System.out.println("Archivo clonado exitosamente en '" + ARCHIVO_CLON + "' (" + clonados + " registros).");
                    break;
                case 4:
                    gestorArchivos.guardarVehiculos(ARCHIVO_MAESTRO, concesionaria.getTodos());
                    int reciclados = gestorArchivos.reciclarArchivoPorEstado(ARCHIVO_MAESTRO, ARCHIVO_RECICLADO, Estado.NUEVO);
                    System.out.println("Archivo reciclado en '" + ARCHIVO_RECICLADO + "': se conservaron "
                            + reciclados + " vehículos 0 KM.");
                    listarVehiculos("CONTENIDO DEL ARCHIVO RECICLADO (0 KM)", gestorArchivos.leerVehiculos(ARCHIVO_RECICLADO));
                    break;
                case 5:
                    gestorArchivos.guardarVehiculos(ARCHIVO_MAESTRO, concesionaria.getTodos());
                    List<Vehiculo> loteNovedades = generarLoteNovedadesDemo();
                    gestorArchivos.guardarVehiculos(ARCHIVO_NOVEDADES, loteNovedades);
                    int totalApareado = gestorArchivos.aparearArchivos(ARCHIVO_MAESTRO, ARCHIVO_NOVEDADES, ARCHIVO_APAREADO);
                    System.out.println("Apareo completado en '" + ARCHIVO_APAREADO + "'. Total consolidado: " + totalApareado + " unidades.");
                    listarVehiculos("CONTENIDO DEL ARCHIVO APAREADO", gestorArchivos.leerVehiculos(ARCHIVO_APAREADO));
                    break;
                default:
                    System.out.println("Opción inválida.");
                    break;
            }
        } catch (IOException e) {
            System.out.println("[ERROR DE E/S DE ARCHIVO]: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.out.println("[ERROR DE SERIALIZACIÓN]: Clase no encontrada - " + e.getMessage());
        } catch (DatoInvalidoException e) {
            System.out.println("[ERROR DE DATOS EN ARCHIVO]: " + e.getMessage());
        }
    }

    /**
     * Genera una lista de novedades de ejemplo (una unidad nueva y una actualización de precio)
     * para demostrar el apareo contra el archivo maestro.
     *
     * @return Lista de novedades de vehículos.
     * @throws DatoInvalidoException Si falla la instanciación de algún vehículo.
     */
    private static List<Vehiculo> generarLoteNovedadesDemo() throws DatoInvalidoException {
        List<Vehiculo> novedades = new ArrayList<>();
        // Actualiza precio de una patente existente en maestro (AG123CD)
        novedades.add(new Auto("Toyota", "Corolla", 2024, "AG123CD", "Blanco", 0, 29500.0, 4, TipoCarroceria.SEDAN));
        // Incorpora una nueva unidad al maestro
        novedades.add(new Camioneta("Ford", "Ranger", 2025, "AH555KL", "Gris Plata", 0, 46000.0, TipoCarroceria.PICKUP, 1050.0, true));
        return novedades;
    }

    /**
     * Guía al usuario paso a paso por consola para dar de alta un nuevo vehículo.
     */
    private static void registrarVehiculoConsola() {
        try {
            System.out.println("\n--- ALTA DE NUEVO VEHÍCULO (Ingrese 0 en cualquier paso para volver) ---");
            System.out.println("Seleccione el tipo de vehículo:");
            System.out.println(" 1. Automóvil");
            System.out.println(" 2. Camioneta");
            System.out.println(" 3. Motocicleta");
            System.out.println(" 0. Volver al menú principal");

            Integer tipo = leerEntero("Tipo (0-3): ", true);
            if (tipo == null) {
                System.out.println("Operación cancelada. Volviendo al menú principal...");
                return;
            }
            if (tipo < 1 || tipo > 3) {
                System.out.println("Tipo inválido. Volviendo al menú principal...");
                return;
            }

            String[] marcaYModelo = seleccionarOIngresarMarcaModelo();
            if (marcaYModelo == null) {
                System.out.println("Operación cancelada. Volviendo al menú principal...");
                return;
            }
            String marca = marcaYModelo[0];
            String modelo = marcaYModelo[1];

            Integer anio = leerEntero("Año (" + Vehiculo.ANIO_MINIMO + "-" + Vehiculo.anioMaximo() + ") [0 para volver]: ", true);
            if (anio == null) {
                System.out.println("Operación cancelada. Volviendo al menú principal...");
                return;
            }

            String patente = leerTextoConCancelacion("Patente (ej: ABC123 o AB123CD) [0 para volver]: ");
            if (patente == null) {
                System.out.println("Operación cancelada. Volviendo al menú principal...");
                return;
            }

            String color = leerTextoConCancelacion("Color [0 para volver]: ");
            if (color == null) {
                System.out.println("Operación cancelada. Volviendo al menú principal...");
                return;
            }

            Integer km = leerEntero("Kilometraje (0 para unidad 0 KM) [-1 para volver]: ", false);
            if (km == null || km == -1) {
                System.out.println("Operación cancelada. Volviendo al menú principal...");
                return;
            }

            Double precioBase = leerDouble("Precio base de lista ($) [0 para volver]: ", true);
            if (precioBase == null) {
                System.out.println("Operación cancelada. Volviendo al menú principal...");
                return;
            }

            Vehiculo nuevo = null;

            if (tipo == 1) {
                Integer puertas = leerEntero("Cantidad de puertas (3 a 5) [0 para volver]: ", true);
                if (puertas == null) {
                    System.out.println("Operación cancelada. Volviendo al menú principal...");
                    return;
                }
                TipoCarroceria carroceria = elegirOpcionEnum("Carrocería de Automóvil", TipoCarroceria.paraAutos());
                if (carroceria == null) {
                    System.out.println("Operación cancelada. Volviendo al menú principal...");
                    return;
                }
                nuevo = new Auto(marca, modelo, anio, patente, color, km, precioBase, puertas, carroceria);
            } else if (tipo == 2) {
                TipoCarroceria carroceria = elegirOpcionEnum("Carrocería de Camioneta", TipoCarroceria.paraCamionetas());
                if (carroceria == null) {
                    System.out.println("Operación cancelada. Volviendo al menú principal...");
                    return;
                }
                Double carga = leerDouble("Capacidad de carga (kg) [0 para volver]: ", true);
                if (carga == null) {
                    System.out.println("Operación cancelada. Volviendo al menú principal...");
                    return;
                }
                String resp4x4 = leerTextoConCancelacion("¿Posee tracción 4x4? (S/N) [0 para volver]: ");
                if (resp4x4 == null) {
                    System.out.println("Operación cancelada. Volviendo al menú principal...");
                    return;
                }
                boolean es4x4 = resp4x4.equalsIgnoreCase("S");
                nuevo = new Camioneta(marca, modelo, anio, patente, color, km, precioBase, carroceria, carga, es4x4);
            } else {
                Integer cilindrada = leerEntero("Cilindrada (cc) [0 para volver]: ", true);
                if (cilindrada == null) {
                    System.out.println("Operación cancelada. Volviendo al menú principal...");
                    return;
                }
                TipoCarroceriaMoto tipoMoto = elegirOpcionEnum("Estilo de Motocicleta", TipoCarroceriaMoto.values());
                if (tipoMoto == null) {
                    System.out.println("Operación cancelada. Volviendo al menú principal...");
                    return;
                }
                nuevo = new Motocicleta(marca, modelo, anio, patente, color, km, precioBase, cilindrada, tipoMoto);
            }

            concesionaria.agregarVehiculo(nuevo);
            System.out.println("Vehículo registrado exitosamente:");
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
     * Permite elegir una marca y modelo existentes del catálogo o cargar nuevos.
     *
     * @return Arreglo de dos posiciones {@code [marca, modelo]}, o {@code null} si se cancela.
     */
    private static String[] seleccionarOIngresarMarcaModelo() {
        List<String> marcas = concesionaria.getMarcasDisponibles();
        System.out.println("\nSeleccione una Marca del catálogo:");
        for (int i = 0; i < marcas.size(); i++) {
            System.out.printf(" %d. %s%n", (i + 1), marcas.get(i));
        }
        System.out.println(" " + (marcas.size() + 1) + ". [Ingresar nueva marca manualmente]");
        System.out.println(" 0. Volver al menú principal");

        Integer opMarca = leerEntero("Opción de marca: ", true);
        if (opMarca == null) {
            return null;
        }

        String marcaElegida;
        if (opMarca >= 1 && opMarca <= marcas.size()) {
            marcaElegida = marcas.get(opMarca - 1);
        } else if (opMarca == marcas.size() + 1) {
            marcaElegida = leerTextoConCancelacion("Ingrese el nombre de la nueva Marca [0 para volver]: ");
            if (marcaElegida == null) return null;
        } else {
            System.out.println("Opción inválida.");
            return null;
        }

        List<String> modelos = concesionaria.getModelosPorMarca(marcaElegida);
        String modeloElegido;
        if (!modelos.isEmpty()) {
            System.out.println("\nModelos registrados para " + marcaElegida + ":");
            for (int i = 0; i < modelos.size(); i++) {
                System.out.printf(" %d. %s%n", (i + 1), modelos.get(i));
            }
            System.out.println(" " + (modelos.size() + 1) + ". [Ingresar nuevo modelo manualmente]");
            System.out.println(" 0. Volver al menú principal");

            Integer opModelo = leerEntero("Opción de modelo: ", true);
            if (opModelo == null) {
                return null;
            }

            if (opModelo >= 1 && opModelo <= modelos.size()) {
                modeloElegido = modelos.get(opModelo - 1);
            } else if (opModelo == modelos.size() + 1) {
                modeloElegido = leerTextoConCancelacion("Ingrese el nombre del nuevo Modelo [0 para volver]: ");
                if (modeloElegido == null) return null;
            } else {
                System.out.println("Opción inválida.");
                return null;
            }
        } else {
            modeloElegido = leerTextoConCancelacion("Ingrese el Modelo para " + marcaElegida + " [0 para volver]: ");
            if (modeloElegido == null) return null;
        }

        return new String[] { marcaElegida, modeloElegido };
    }

    /**
     * Muestra el catálogo actual de marcas y modelos y permite añadir nuevas entradas.
     */
    private static void gestionarCatalogoMarcasModelos() {
        System.out.println("\n--- CATÁLOGO ACTUAL DE MARCAS Y MODELOS ---");
        for (Map.Entry<String, List<String>> entrada : concesionaria.getCatalogoMarcas().entrySet()) {
            System.out.println(" * " + entrada.getKey() + ": " + entrada.getValue());
        }

        System.out.println("\n 1. Agregar nueva Marca o Modelo");
        System.out.println(" 0. Volver al menú principal");
        Integer op = leerEntero("Seleccione opción: ", true);
        if (op == null) {
            System.out.println("Volviendo al menú principal...");
            return;
        }
        if (op == 1) {
            String marca = leerTextoConCancelacion("Ingrese el nombre de la Marca (existente o nueva) [0 para volver]: ");
            if (marca == null) {
                System.out.println("Volviendo al menú principal...");
                return;
            }
            String modelo = leerTextoConCancelacion("Ingrese el nombre del Modelo nuevo [0 para volver]: ");
            if (modelo == null) {
                System.out.println("Volviendo al menú principal...");
                return;
            }
            concesionaria.registrarMarcaYModelo(marca, modelo);
            System.out.println("Catálogo actualizado: " + marca + " -> " + modelo);
        }
    }

    /**
     * Filtra y lista vehículos según su categoría (Automóvil, Camioneta o Motocicleta).
     */
    private static void listarPorCategoria() {
        System.out.println("\n--- FILTRAR POR CATEGORÍA ---");
        System.out.println(" 1. Automóviles");
        System.out.println(" 2. Camionetas");
        System.out.println(" 3. Motocicletas");
        System.out.println(" 0. Volver al menú principal");
        Integer op = leerEntero("Seleccione categoría: ", true);
        if (op == null) {
            System.out.println("Volviendo al menú principal...");
            return;
        }
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

    /**
     * Filtra y lista vehículos según su estado comercial (0 KM o Usado).
     */
    private static void listarPorEstado() {
        System.out.println("\n--- FILTRAR POR ESTADO COMERCIAL ---");
        System.out.println(" 1. Nuevos (0 KM)");
        System.out.println(" 2. Usados");
        System.out.println(" 0. Volver al menú principal");
        Integer op = leerEntero("Seleccione estado: ", true);
        if (op == null) {
            System.out.println("Volviendo al menú principal...");
            return;
        }
        if (op == 1) {
            listarVehiculos("VEHÍCULOS NUEVOS (0 KM)", concesionaria.getPorEstado(Estado.NUEVO));
        } else if (op == 2) {
            listarVehiculos("VEHÍCULOS USADOS", concesionaria.getPorEstado(Estado.USADO));
        } else {
            System.out.println("Estado inválido.");
        }
    }

    /**
     * Imprime una lista genérica acotada superiormente de vehículos con un encabezado.
     *
     * @param titulo Encabezado del listado.
     * @param lista  Colección de vehículos o subtipos de {@link Vehiculo}.
     */
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

    /**
     * Solicita una patente por consola y muestra los datos de la unidad si existe.
     */
    private static void buscarPorPatenteConsola() {
        String patente = leerTextoConCancelacion("\nIngrese la patente a buscar [0 para volver]: ");
        if (patente == null) {
            System.out.println("Volviendo al menú principal...");
            return;
        }
        Vehiculo encontrado = concesionaria.buscarPorPatente(patente);
        if (encontrado != null) {
            System.out.println("Vehículo encontrado: " + encontrado);
        } else {
            System.out.println("No existe ningún vehículo en stock con la patente: " + patente);
        }
    }

    /**
     * Gestiona el proceso de venta de un vehículo por patente previa confirmación del operador.
     */
    private static void venderVehiculoConsola() {
        if (concesionaria.estaVacio()) {
            System.out.println("\nNo hay vehículos en stock para vender.");
            return;
        }
        try {
            String patente = leerTextoConCancelacion("\nIngrese la patente del vehículo a vender [0 para volver]: ");
            if (patente == null) {
                System.out.println("Operación cancelada. Volviendo al menú principal...");
                return;
            }
            Vehiculo v = concesionaria.buscarPorPatente(patente);
            if (v != null) {
                System.out.println("Unidad seleccionada: " + v);
                String confirmacion = leerTextoConCancelacion("¿Confirmar venta? (S/N) [0 para volver]: ");
                if (confirmacion == null) {
                    System.out.println("Operación cancelada. Volviendo al menú principal...");
                    return;
                }
                if (confirmacion.equalsIgnoreCase("S")) {
                    double precioFinal = concesionaria.venderVehiculo(patente);
                    System.out.printf("¡VENTA CONCRETADA! Precio final cobrado: $%.2f%n", precioFinal);
                } else {
                    System.out.println("Venta no confirmada. Volviendo al menú principal...");
                }
            } else {
                System.out.println("No se encontró el vehículo con patente: " + patente);
            }
        } catch (DatoInvalidoException e) {
            System.out.println("[ERROR EN VENTA]: " + e.getMessage());
        }
    }

    /**
     * Método genérico para seleccionar un elemento desde un arreglo de opciones enum.
     *
     * @param <T>      Tipo de las opciones presentadas.
     * @param titulo   Título del menú de selección.
     * @param opciones Arreglo de opciones disponibles.
     * @return El elemento elegido, o {@code null} si el usuario cancela con 0.
     */
    private static <T> T elegirOpcionEnum(String titulo, T[] opciones) {
        while (true) {
            System.out.println("\n" + titulo + ":");
            for (int i = 0; i < opciones.length; i++) {
                System.out.printf(" %d. %s%n", (i + 1), opciones[i]);
            }
            System.out.println(" 0. Volver al menú principal");
            Integer sel = leerEntero("Seleccione opción (0-" + opciones.length + "): ", true);
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
     * Lee un número entero validando formato y opción de cancelación con 0.
     *
     * @param mensaje         Mensaje mostrado al usuario.
     * @param cancelarConCero Indica si ingresar 0 devuelve {@code null}.
     * @return Entero leído, o {@code null} si se canceló.
     */
    private static Integer leerEntero(String mensaje, boolean cancelarConCero) {
        while (true) {
            System.out.print(mensaje);
            try {
                int valor = Integer.parseInt(scanner.nextLine().trim());
                if (cancelarConCero && valor == 0) {
                    return null;
                }
                return valor;
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Por favor ingrese un número entero.");
            }
        }
    }

    /**
     * Lee un número decimal validando formato y opción de cancelación con 0.
     *
     * @param mensaje         Mensaje mostrado al usuario.
     * @param cancelarConCero Indica si ingresar 0 devuelve {@code null}.
     * @return Valor decimal leído, o {@code null} si se canceló.
     */
    private static Double leerDouble(String mensaje, boolean cancelarConCero) {
        while (true) {
            System.out.print(mensaje);
            try {
                double valor = Double.parseDouble(scanner.nextLine().trim().replace(",", "."));
                if (cancelarConCero && valor == 0) {
                    return null;
                }
                return valor;
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Por favor ingrese un número válido.");
            }
        }
    }

    /**
     * Lee una cadena no vacía permitiendo cancelar con "0".
     *
     * @param mensaje Mensaje mostrado al usuario.
     * @return Texto ingresado, o {@code null} si se ingresó "0".
     */
    private static String leerTextoConCancelacion(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String texto = scanner.nextLine().trim();
            if (texto.equals("0")) {
                return null;
            }
            if (!texto.isEmpty()) {
                return texto;
            }
            System.out.println("Este campo no puede quedar vacío (o ingrese 0 para volver).");
        }
    }

    /**
     * Precarga un lote inicial de vehículos de demostración en el inventario.
     */
    private static void cargarInventarioInicial() {
        try {
            concesionaria.agregarVehiculo(new Auto("Toyota", "Corolla", 2024, "AG123CD", "Blanco", 0, 28000.0, 4, TipoCarroceria.SEDAN));
            concesionaria.agregarVehiculo(new Auto("Ford", "Focus", 2019, "AD456EF", "Gris", 62000, 16500.0, 5, TipoCarroceria.HATCHBACK));
            concesionaria.agregarVehiculo(new Camioneta("Toyota", "Hilux", 2023, "AF789GH", "Negro", 15000, 42000.0, TipoCarroceria.PICKUP, 1000.0, true));
            concesionaria.agregarVehiculo(new Camioneta("Volkswagen", "Amarok", 2025, "AG999ZZ", "Azul", 0, 48000.0, TipoCarroceria.PICKUP, 1100.0, true));
            concesionaria.agregarVehiculo(new Motocicleta("Honda", "CB 250 Twister", 2024, "A123BCD", "Rojo", 0, 5200.0, 250, TipoCarroceriaMoto.NAKED));
            concesionaria.agregarVehiculo(new Motocicleta("Yamaha", "XTZ 125", 2021, "A098XYZ", "Blanco", 18500, 3100.0, 125, TipoCarroceriaMoto.ENDURO));
        } catch (DatoInvalidoException e) {
            System.err.println("Error al precargar inventario: " + e.getMessage());
        }
    }
}