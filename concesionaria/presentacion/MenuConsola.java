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
import java.util.List;
import java.util.Scanner;

/**
 * Vista de consola de la capa de PRESENTACIÓN para GyQ Concesionaria.
 * Gestiona la interacción por teclado con el operador, valida entradas,
 * invoca los servicios de {@link Concesionaria}, {@link TallerGeneral} y
 * persiste los cambios mediante {@link GestorArchivosVehiculos}.
 *
 * @author Camila Quintero
 * @author Guadalupe Gallardo
 * @version 3.1
 */
public class MenuConsola {

    private static final String ARCHIVO_MAESTRO = "inventario_maestro.dat";

    private final Scanner scanner;
    private final Concesionaria concesionaria;
    private final GestorArchivosVehiculos gestorArchivos;
    private final TallerGeneral<Vehiculo> tallerGeneral;

    /**
     * Inicializa la vista de consola junto con el lector de teclado
     * y los componentes de la capa de negocio.
     */
    public MenuConsola() {
        this.scanner = new Scanner(System.in);
        this.concesionaria = new Concesionaria();
        this.gestorArchivos = new GestorArchivosVehiculos();
        this.tallerGeneral = new TallerGeneral<>();
    }

    /**
     * Recupera el inventario persistido en disco al iniciar la aplicación.
     */
    public void cargarInventario() {
        try {
            List<Vehiculo> desdeArchivo = gestorArchivos.leerVehiculos(ARCHIVO_MAESTRO);
            if (!desdeArchivo.isEmpty()) {
                concesionaria.reemplazarInventario(desdeArchivo);
            }
        } catch (IOException | ClassNotFoundException | DatoInvalidoException e) {
            System.err.println("Error al cargar inventario desde disco: " + e.getMessage());
        }
    }

    /**
     * Imprime en consola el encabezado con el stock actual, el total recaudado
     * y las opciones disponibles del menú principal.
     */
    public void mostrarMenuPrincipal() {
        long recaudadoEntero = Math.round(concesionaria.getTotalRecaudadoVentas());
        System.out.println("\n=======================================================");
        System.out.println("          GyQ CONCESIONARIA MULTIMARCA - VENTAS        ");
        System.out.println("=======================================================");
        System.out.println(" Stock: " + concesionaria.cantidadEnStock() + " unidades | Recaudado: $" + recaudadoEntero);
        System.out.println("-------------------------------------------------------");
        System.out.println(" 1. Registrar nuevo vehículo en stock");
        System.out.println(" 2. Catálogo de Marcas y Modelos");
        System.out.println(" 3. Listar todo el inventario (ordenado por año)");
        System.out.println(" 4. Listar vehículos por categoría");
        System.out.println(" 5. Listar vehículos por estado (0 KM / Usado)");
        System.out.println(" 6. Buscar vehículo por patente");
        System.out.println(" 7. Registrar venta de vehículo");
        System.out.println(" 8. Modificar datos de un vehículo");
        System.out.println(" 9. Enviar vehículo a Taller General");
        System.out.println(" 0. Guardar y Salir");
        System.out.println("-------------------------------------------------------");
    }

    /**
     * Solicita y lee la opción numérica elegida por el operador en el menú principal.
     *
     * @return Número entero ingresado, o {@code -1} si la entrada no es válida.
     */
    public int leerOpcionPrincipal() {
        Integer op = leerEntero("Seleccione una opción: ", false);
        return (op != null) ? op : -1;
    }

    /**
     * Solicita los datos por consola para dar de alta un nuevo {@link Auto},
     * {@link Camioneta} o {@link Motocicleta}, lo agrega al inventario y guarda en disco.
     */
    public void registrarVehiculo() {
        try {
            System.out.println("\n--- ALTA DE NUEVO VEHÍCULO ('Z' para cancelar) ---");
            System.out.println(" 1. Automóvil | 2. Camioneta | 3. Motocicleta");

            Integer tipo = leerEntero("Tipo: ", true);
            if (tipo == null) return;
            if (tipo < 1 || tipo > 3) {
                System.out.println("Tipo inválido.");
                return;
            }

            String[] marcaYModelo = seleccionarOIngresarMarcaModelo();
            if (marcaYModelo == null) return;

            Integer anio = leerEntero("Año (" + Vehiculo.ANIO_MINIMO + "-" + Vehiculo.anioMaximo() + "): ", true);
            if (anio == null) return;

            String patente = leerTexto("Patente (ej: ABC123 o AB123CD): ");
            if (patente == null) return;

            String color = leerTexto("Color: ");
            if (color == null) return;

            Integer km = leerEntero("Kilometraje (0 para 0 KM): ", true);
            if (km == null) return;

            Double precioBase = leerDouble("Precio base de lista ($): ", true);
            if (precioBase == null) return;

            Vehiculo nuevo = crearVehiculoPorTipo(tipo, marcaYModelo[0], marcaYModelo[1], anio, patente, color, km, precioBase);
            if (nuevo == null) return;

            concesionaria.agregarVehiculo(nuevo);
            guardarCambios();
            System.out.println("\nVehículo guardado: " + nuevo);

        } catch (PatenteInvalidaException e) {
            System.out.println("[ERROR DE PATENTE]: " + e.getMessage());
        } catch (PuertasInsuficientesException e) {
            System.out.println("[ERROR EN AUTOMÓVIL]: " + e.getMessage());
        } catch (DatoInvalidoException e) {
            System.out.println("[DATO INVÁLIDO]: " + e.getMessage());
        }
    }

    /**
     * Muestra el catálogo actual de marcas y modelos disponibles y permite
     * registrar nuevas combinaciones.
     */
    public void gestionarCatalogo() {
        System.out.println("\n--- CATÁLOGO DE MARCAS Y MODELOS ---");
        for (String marca : concesionaria.getMarcasDisponibles()) {
            System.out.println(" * " + marca + ": " + concesionaria.getModelosPorMarca(marca));
        }
        System.out.println("\n 1. Agregar Marca/Modelo | Z. Volver");
        Integer op = leerEntero("Opción: ", true);
        if (op != null && op == 1) {
            String marca = leerTexto("Marca: ");
            if (marca == null) return;
            String modelo = leerTexto("Modelo: ");
            if (modelo == null) return;
            concesionaria.registrarMarcaYModelo(marca, modelo);
            System.out.println("Catálogo actualizado.");
        }
    }

    /**
     * Muestra por pantalla todo el inventario ordenado cronológicamente por año.
     */
    public void listarInventarioCompleto() {
        listarVehiculos("INVENTARIO COMPLETO (ORDENADO POR AÑO)", concesionaria.getInventarioOrdenadoPorAnio());
    }

    /**
     * Permite filtrar y mostrar el inventario según su tipo (Automóvil, Camioneta o Motocicleta).
     */
    public void listarPorCategoria() {
        System.out.println("\n 1. Automóviles | 2. Camionetas | 3. Motocicletas | Z. Volver");
        Integer op = leerEntero("Categoría: ", true);
        if (op == null) return;

        if (op == 1) listarVehiculos("AUTOMÓVILES", concesionaria.getAutos());
        else if (op == 2) listarVehiculos("CAMIONETAS", concesionaria.getCamionetas());
        else if (op == 3) listarVehiculos("MOTOCICLETAS", concesionaria.getMotos());
        else System.out.println("Categoría inválida.");
    }

    /**
     * Permite filtrar y mostrar los vehículos según su estado comercial ({@link Estado#NUEVO} o {@link Estado#USADO}).
     */
    public void listarPorEstado() {
        System.out.println("\n 1. Nuevos (0 KM) | 2. Usados | Z. Volver");
        Integer op = leerEntero("Estado: ", true);
        if (op == null) return;

        if (op == 1) listarVehiculos("NUEVOS (0 KM)", concesionaria.getPorEstado(Estado.NUEVO));
        else if (op == 2) listarVehiculos("USADOS", concesionaria.getPorEstado(Estado.USADO));
        else System.out.println("Estado inválido.");
    }

    /**
     * Solicita una patente por teclado y muestra el vehículo encontrado en stock.
     */
    public void buscarPorPatente() {
        String patente = leerTexto("\nPatente a buscar ('Z' para volver): ");
        if (patente == null) return;
        Vehiculo v = concesionaria.buscarPorPatente(patente);
        System.out.println(v != null ? "Encontrado: " + v : "No existe un vehículo con patente: " + patente);
    }

    /**
     * Gestiona la venta de un vehículo por patente, actualiza el stock y persiste los cambios.
     */
    public void venderVehiculo() {
        if (concesionaria.estaVacio()) {
            System.out.println("\nNo hay vehículos en stock.");
            return;
        }
        try {
            String patente = leerTexto("\nPatente a vender ('Z' para volver): ");
            if (patente == null) return;

            Vehiculo v = concesionaria.buscarPorPatente(patente);
            if (v == null) {
                System.out.println("No se encontró la patente: " + patente);
                return;
            }
            System.out.println("Unidad: " + v);
            String conf = leerTexto("¿Confirmar venta? (S/N): ");
            if (conf != null && conf.equalsIgnoreCase("S")) {
                double precioFinal = concesionaria.venderVehiculo(patente);
                guardarCambios();
                System.out.println("¡VENTA CONCRETADA! Cobrado: $" + Math.round(precioFinal));
            }
        } catch (DatoInvalidoException e) {
            System.out.println("[ERROR EN VENTA]: " + e.getMessage());
        }
    }

    /**
     * Permite editar los atributos comunes o específicos de un vehículo registrado
     * y guarda los cambios en disco.
     */
    public void modificarVehiculo() {
        if (concesionaria.estaVacio()) {
            System.out.println("\nNo hay vehículos en stock.");
            return;
        }
        String patente = leerTexto("\nPatente del vehículo a modificar ('Z' para volver): ");
        if (patente == null) return;

        Vehiculo v = concesionaria.buscarPorPatente(patente);
        if (v == null) {
            System.out.println("No se encontró la patente: " + patente);
            return;
        }

        System.out.println("\nActual: " + v);
        System.out.println(" 1. Modificar kilometraje");
        System.out.println(" 2. Modificar precio base");
        System.out.println(" 3. Modificar color");

        if (v instanceof Auto) {
            System.out.println(" 4. Modificar cantidad de puertas");
        } else if (v instanceof Camioneta) {
            System.out.println(" 4. Modificar capacidad de carga (kg)");
            System.out.println(" 5. Modificar tracción 4x4 (S/N)");
        } else if (v instanceof Motocicleta) {
            System.out.println(" 4. Modificar cilindrada (cc)");
        }
        System.out.println(" Z. Cancelar");

        Integer op = leerEntero("Opción: ", true);
        if (op == null) return;

        try {
            if (aplicarModificacion(v, op)) {
                guardarCambios();
                System.out.println("Vehículo actualizado: " + v);
            }
        } catch (DatoInvalidoException e) {
            System.out.println("[DATO INVÁLIDO]: " + e.getMessage());
        }
    }

    /**
     * Busca un vehículo por patente y lo envía a {@link TallerGeneral} para su alistamiento.
     */
    public void enviarATaller() {
        String patente = leerTexto("\nPatente para Taller General ('Z' para volver): ");
        if (patente == null) return;
        Vehiculo v = concesionaria.buscarPorPatente(patente);
        if (v != null) tallerGeneral.procesarVehiculo(v);
        else System.out.println("No existe la patente: " + patente);
    }

    /**
     * Guarda el estado actual del inventario en disco y libera el flujo de entrada estándar.
     */
    public void guardarYSalir() {
        guardarCambios();
        scanner.close();
        System.out.println("\nDatos guardados en '" + ARCHIVO_MAESTRO + "'. Cerrando sistema.");
    }

    // --- MÉTODOS AUXILIARES PRIVADOS ---

    private Vehiculo crearVehiculoPorTipo(int tipo, String marca, String modelo, Integer anio,
                                          String patente, String color, Integer km, Double precioBase)
            throws DatoInvalidoException {
        if (tipo == 1) {
            Integer puertas = leerEntero("Cantidad de puertas (1 a 5): ", true);
            if (puertas == null) return null;

            Object carr = elegirCarroceria("Carrocería de Automóvil", TipoCarroceria.paraAutos());
            if (carr == null) return null;

            return (carr instanceof TipoCarroceria)
                    ? new Auto(marca, modelo, anio, patente, color, km, precioBase, puertas, (TipoCarroceria) carr)
                    : new Auto(marca, modelo, anio, patente, color, km, precioBase, puertas, (String) carr);
        }

        if (tipo == 2) {
            Object carr = elegirCarroceria("Carrocería de Camioneta", TipoCarroceria.paraCamionetas());
            if (carr == null) return null;

            Double carga = leerDouble("Capacidad de carga (kg): ", true);
            if (carga == null) return null;

            String resp4x4 = leerTexto("¿Posee tracción 4x4? (S/N): ");
            if (resp4x4 == null) return null;

            boolean es4x4 = resp4x4.equalsIgnoreCase("S");
            return (carr instanceof TipoCarroceria)
                    ? new Camioneta(marca, modelo, anio, patente, color, km, precioBase, (TipoCarroceria) carr, carga, es4x4)
                    : new Camioneta(marca, modelo, anio, patente, color, km, precioBase, (String) carr, carga, es4x4);
        }

        Integer cilindrada = leerEntero("Cilindrada (cc): ", true);
        if (cilindrada == null) return null;

        TipoCarroceriaMoto tipoMoto = elegirTipoMoto();
        if (tipoMoto == null) return null;

        return new Motocicleta(marca, modelo, anio, patente, color, km, precioBase, cilindrada, tipoMoto);
    }

    private Object elegirCarroceria(String titulo, TipoCarroceria[] opciones) {
        System.out.println("\n" + titulo + ":");
        for (int i = 0; i < opciones.length; i++) {
            System.out.println(" " + (i + 1) + ". " + opciones[i]);
        }
        int opcionOtra = opciones.length + 1;
        System.out.println(" " + opcionOtra + ". Otra (Ingresar nueva)");

        Integer sel = leerEntero("Seleccione opción: ", true);
        if (sel == null) return null;
        if (sel >= 1 && sel <= opciones.length) return opciones[sel - 1];
        if (sel == opcionOtra) return leerTexto("Nombre de la nueva carrocería: ");

        System.out.println("Opción inválida.");
        return null;
    }

    private TipoCarroceriaMoto elegirTipoMoto() {
        TipoCarroceriaMoto[] opciones = TipoCarroceriaMoto.values();
        System.out.println("\nEstilo de Motocicleta:");
        for (int i = 0; i < opciones.length; i++) {
            System.out.println(" " + (i + 1) + ". " + opciones[i]);
        }
        Integer sel = leerEntero("Seleccione opción: ", true);
        if (sel == null) return null;
        if (sel >= 1 && sel <= opciones.length) return opciones[sel - 1];

        System.out.println("Opción inválida.");
        return null;
    }

    private String[] seleccionarOIngresarMarcaModelo() {
        List<String> marcas = concesionaria.getMarcasDisponibles();
        System.out.println("\nSeleccione una Marca:");
        for (int i = 0; i < marcas.size(); i++) {
            System.out.println(" " + (i + 1) + ". " + marcas.get(i));
        }
        System.out.println(" " + (marcas.size() + 1) + ". Agregar nueva marca");

        Integer opMarca = leerEntero("Opción: ", true);
        if (opMarca == null) return null;

        String marca;
        if (opMarca >= 1 && opMarca <= marcas.size()) {
            marca = marcas.get(opMarca - 1);
        } else if (opMarca == marcas.size() + 1) {
            marca = leerTexto("Nueva Marca: ");
            if (marca == null) return null;
        } else {
            System.out.println("Opción inválida.");
            return null;
        }

        List<String> modelos = concesionaria.getModelosPorMarca(marca);
        if (modelos.isEmpty()) {
            String modelo = leerTexto("Modelo para " + marca + ": ");
            return modelo == null ? null : new String[] { marca, modelo };
        }

        System.out.println("\nModelos de " + marca + ":");
        for (int i = 0; i < modelos.size(); i++) {
            System.out.println(" " + (i + 1) + ". " + modelos.get(i));
        }
        System.out.println(" " + (modelos.size() + 1) + ". Agregar nuevo modelo");

        Integer opModelo = leerEntero("Opción: ", true);
        if (opModelo == null) return null;

        if (opModelo >= 1 && opModelo <= modelos.size()) {
            return new String[] { marca, modelos.get(opModelo - 1) };
        } else if (opModelo == modelos.size() + 1) {
            String modelo = leerTexto("Nuevo Modelo: ");
            return modelo == null ? null : new String[] { marca, modelo };
        }

        System.out.println("Opción inválida.");
        return null;
    }

    private void listarVehiculos(String titulo, List<? extends Vehiculo> lista) {
        System.out.println("\n--- " + titulo + " (" + lista.size() + ") ---");
        if (lista.isEmpty()) {
            System.out.println("No hay vehículos registrados.");
            return;
        }
        for (Vehiculo v : lista) {
            System.out.println(" * " + v);
        }
    }

    private boolean aplicarModificacion(Vehiculo v, int op) throws DatoInvalidoException {
        if (op == 1) {
            Integer nuevoKm = leerEntero("Nuevo kilometraje: ", true);
            if (nuevoKm == null) return false;
            v.setKilometraje(nuevoKm);
            return true;
        }
        if (op == 2) {
            Double nuevoPrecio = leerDouble("Nuevo precio base ($): ", true);
            if (nuevoPrecio == null) return false;
            v.setPrecioBase(nuevoPrecio);
            return true;
        }
        if (op == 3) {
            String nuevoColor = leerTexto("Nuevo color: ");
            if (nuevoColor == null) return false;
            v.setColor(nuevoColor);
            return true;
        }
        if (op == 4 && v instanceof Auto) {
            Integer nuevasPuertas = leerEntero("Nueva cantidad de puertas (1 a 5): ", true);
            if (nuevasPuertas == null) return false;
            ((Auto) v).setCantPuertas(nuevasPuertas);
            return true;
        }
        if (op == 4 && v instanceof Camioneta) {
            Double nuevaCarga = leerDouble("Nueva capacidad de carga (kg): ", true);
            if (nuevaCarga == null) return false;
            ((Camioneta) v).setCapacidadCargaKg(nuevaCarga);
            return true;
        }
        if (op == 5 && v instanceof Camioneta) {
            String resp4x4 = leerTexto("¿Posee tracción 4x4? (S/N): ");
            if (resp4x4 == null) return false;
            ((Camioneta) v).setTraccion4x4(resp4x4.equalsIgnoreCase("S"));
            return true;
        }
        if (op == 4 && v instanceof Motocicleta) {
            Integer nuevaCilindrada = leerEntero("Nueva cilindrada (cc): ", true);
            if (nuevaCilindrada == null) return false;
            ((Motocicleta) v).setCilindrada(nuevaCilindrada);
            return true;
        }

        System.out.println("Opción inválida.");
        return false;
    }

    private Integer leerEntero(String mensaje, boolean permitirZ) {
        while (true) {
            System.out.print(mensaje);
            String entrada = scanner.nextLine().trim();
            if (permitirZ && entrada.equalsIgnoreCase("Z")) return null;
            try {
                return Integer.parseInt(entrada);
            } catch (NumberFormatException e) {
                System.out.println("Ingrese un número entero válido" + (permitirZ ? " (o 'Z' para volver)." : "."));
            }
        }
    }

    private Double leerDouble(String mensaje, boolean permitirZ) {
        while (true) {
            System.out.print(mensaje);
            String entrada = scanner.nextLine().trim().replace(",", ".");
            if (permitirZ && entrada.equalsIgnoreCase("Z")) return null;
            try {
                return Double.parseDouble(entrada);
            } catch (NumberFormatException e) {
                System.out.println("Ingrese un importe numérico válido" + (permitirZ ? " (o 'Z' para volver)." : "."));
            }
        }
    }

    private String leerTexto(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String texto = scanner.nextLine().trim();
            if (texto.equalsIgnoreCase("Z")) return null;
            if (!texto.isEmpty()) return texto;
            System.out.println("El campo no puede estar vacío (o ingrese 'Z' para volver).");
        }
    }

    private void guardarCambios() {
        try {
            gestorArchivos.guardarVehiculos(ARCHIVO_MAESTRO, concesionaria.getTodos());
        } catch (IOException e) {
            System.err.println("[ADVERTENCIA]: No se pudo guardar en disco: " + e.getMessage());
        }
    }
}