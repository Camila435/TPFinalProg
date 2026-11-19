package presentacion;

import concesionaria.dominio.*;
import negocio.Concesionaria;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Capa de PRESENTACIÓN: menú interactivo por consola para GyQ Concesionaria.
 * Permite volver al menú principal desde cualquier submenú u operación ingresando 0.
 */
/**
 * POO - CLASE: MainApp agrupa los métodos de interacción con el usuario.
 * ARQUITECTURA POR CAPAS: presenta datos y delega las reglas de negocio
 * al objeto Concesionaria; no administra directamente el inventario.
 */
public class MainApp {

    // ENCAPSULAMIENTO: private impide el acceso directo desde otras clases.
    // static: atributo compartido por los métodos estáticos de esta clase.
    // final: impide reasignar la referencia; no vuelve inmutable al Scanner.
    // Scanner: lee datos desde la entrada estándar (teclado).
    private static final Scanner scanner = new Scanner(System.in);
    // COMPOSICIÓN / COLABORACIÓN ENTRE OBJETOS: la presentación usa
    // una instancia de la capa de negocio creada mediante new.
    private static final Concesionaria concesionaria = new Concesionaria();

    /**
     * PUNTO DE ENTRADA: la JVM inicia la aplicación en main.
     * public: accesible desde fuera; static: no requiere crear MainApp;
     * void: no devuelve valor; String[]: arreglo de argumentos.
     */
    public static void main(String[] args) {
        cargarInventarioInicial();

        int opcion = -1;
        // CICLO DO-WHILE: ejecuta el menú al menos una vez y repite
        // hasta que la opción elegida sea 0.
        do {
            mostrarMenuPrincipal();
            Integer opLeida = leerEntero("Seleccione una opción: ", false);
            opcion = (opLeida != null) ? opLeida : -1;

            // SELECCIÓN MÚLTIPLE: switch deriva a una operación según
            // el valor de opcion; break evita continuar con otro case.
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

    // MÉTODO DE PRESENTACIÓN: System.out.println imprime texto;
    // printf usa formatos: %d entero, %.2f decimal y %n salto de línea.
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
        System.out.println(" 0. Salir");
        System.out.println("-------------------------------------------------------");
    }

    // --- ENTORNO 1: ALTA DE VEHÍCULO (Con salida con 0 en cada paso mediante if) ---

    /**
     * ALTA DE VEHÍCULOS: combina entrada, validación y creación de objetos.
     * try/catch: captura excepciones y evita cerrar el programa ante errores.
     * return: permite cancelar el proceso antes de registrar el vehículo.
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

            // POLIMORFISMO: una variable de tipo Vehiculo (superclase)
            // puede referenciar instancias de sus subclases concretas.
            Vehiculo nuevo = null;

            // CONDICIONALES: se instancia la subclase correspondiente.
            // new invoca el constructor; los argumentos deben coincidir
            // con la firma declarada en cada clase del dominio.
            if (tipo == 1) {
                // La cátedra no exige puertas; el dominio actual no almacena ese dato.

                TipoCarroceria carroceria = elegirOpcionEnum("Carrocería de Automóvil", TipoCarroceria.paraAutos());
                if (carroceria == null) {
                    System.out.println("Operación cancelada. Volviendo al menú principal...");
                    return;
                }
                nuevo = new Automovil(marca, modelo, anio, patente, color, km, precioBase, carroceria);
            } else if (tipo == 2) {
                TipoCarroceria carroceria = elegirOpcionEnum("Carrocería de Camioneta", TipoCarroceria.paraCamionetas());
                if (carroceria == null) {
                    System.out.println("Operación cancelada. Volviendo al menú principal...");
                    return;
                }
                // El dominio actual no define capacidad de carga ni tracción 4x4.

                nuevo = new Camioneta(marca, modelo, anio, patente, color, km, precioBase, carroceria);
            } else {
                // La cilindrada no forma parte del modelo de dominio actual.

                TipoCarroceriaMoto tipoMoto = elegirOpcionEnum("Estilo de Motocicleta", TipoCarroceriaMoto.values());
                if (tipoMoto == null) {
                    System.out.println("Operación cancelada. Volviendo al menú principal...");
                    return;
                }
                nuevo = new Motocicleta(marca, modelo, anio, patente, color, km, precioBase, tipoMoto);
            }

            // DELEGACIÓN: la validación del inventario corresponde a
            // Concesionaria, no a esta capa de presentación.
            concesionaria.agregarVehiculo(nuevo);
            System.out.println("Vehículo registrado exitosamente:");
            System.out.println(" -> " + nuevo);

        // EXCEPCIONES PERSONALIZADAS: los catch más específicos se
        // escriben antes que el catch de una superclase de excepción.
        } catch (PatenteInvalidaException e) {
            System.out.println("[ERROR DE PATENTE]: " + e.getMessage());
        } catch (DatoInvalidoException e) {
            System.out.println("[DATO INVÁLIDO]: " + e.getMessage());
        }
    }

    /**
     * ARREGLOS Y LISTAS GENÉRICAS: List<String> contiene cadenas;
     * String[] devuelve dos valores relacionados (marca y modelo).
     * Los índices de las listas comienzan en cero.
     */
    private static String[] seleccionarOIngresarMarcaModelo() {
        List<String> marcas = concesionaria.getMarcasDisponibles();
        System.out.println("\nSeleccione una Marca del catálogo:");
        // CICLO FOR CON CONTADOR: i recorre las posiciones 0..size()-1;
        // get(i) accede al elemento y se muestra i+1 al usuario.
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

    // --- ENTORNO 2: CATÁLOGO DINÁMICO DE MARCAS Y MODELOS ---

    /**
     * MAP Y GENÉRICOS ANIDADOS: Map<String,List<String>> relaciona
     * cada marca (clave) con su lista de modelos (valor).
     * entrySet() permite recorrer pares clave-valor con for-each.
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

    // --- ENTORNO 3, 4 y 5: CONSULTAS Y FILTROS ---

    // FILTRADO POR TIPO: se invocan métodos de negocio que devuelven
    // List<Automovil>, List<Camioneta> o List<Motocicleta>.
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

    // ENUM: Estado representa un conjunto fijo de constantes,
    // como NUEVO y USADO, y evita valores de texto arbitrarios.
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
     * GENÉRICOS CON COMODÍN ACOTADO: List<? extends Vehiculo> acepta
     * listas de Vehiculo o de cualquier subclase de Vehiculo.
     * Se usa para leer/imprimir elementos; no para insertar vehículos.
     * POLIMORFISMO: imprimir v invoca su toString() efectivo.
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

    // --- ENTORNO 6 Y 7: BÚSQUEDA Y VENTA ---

    // BÚSQUEDA: la interfaz pide la patente y delega la consulta
    // a negocio.Concesionaria; null indica que no hubo coincidencia.
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

    // VENTA: primero consulta stock, luego solicita confirmación;
    // la baja y el cálculo del precio los realiza la capa de negocio.
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

    // --- ENTORNO 8: UTILIDADES DE LECTURA SEGURA Y GENÉRICOS ---

    /**
     * MÉTODO GENÉRICO: <T> declara un parámetro de tipo.
     * T[] acepta arreglos de distintos tipos (aquí, enumeraciones).
     * Devuelve un valor T o null si el usuario cancela.
     * while(true) repite hasta ejecutar un return válido.
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
     * ENTRADA SEGURA: scanner.nextLine() lee una línea completa;
     * trim() quita espacios; Integer.parseInt convierte a entero.
     * NumberFormatException controla entradas no numéricas.
     * Integer (wrapper) permite devolver null al cancelar.
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
     * CONVERSIÓN NUMÉRICA: Double.parseDouble convierte texto a decimal.
     * replace(",", ".") admite coma decimal como entrada.
     * Double es envoltorio de double y admite null para cancelar.
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

    // VALIDACIÓN DE TEXTO: evita cadenas vacías y utiliza "0" como
    // valor centinela para cancelar y retornar null.
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
     * DATOS DE PRUEBA: crea objetos mediante constructores y los
     * registra en el inventario para probar las opciones del menú.
     * Cada new crea una instancia; agregarVehiculo recibe Vehiculo
     * gracias a la relación de herencia (polimorfismo por subtipos).
     */
    private static void cargarInventarioInicial() {
        try {
            concesionaria.agregarVehiculo(new Automovil("Toyota", "Corolla", 2024, "AG123CD", "Blanco", 0, 28000.0, TipoCarroceria.SEDAN));
            concesionaria.agregarVehiculo(new Automovil("Ford", "Focus", 2019, "AD456EF", "Gris", 62000, 16500.0, TipoCarroceria.HATCHBACK));
            concesionaria.agregarVehiculo(new Camioneta("Toyota", "Hilux", 2023, "AF789GH", "Negro", 15000, 42000.0, TipoCarroceria.PICKUP));
            concesionaria.agregarVehiculo(new Camioneta("Volkswagen", "Amarok", 2025, "AG999ZZ", "Azul", 0, 48000.0, TipoCarroceria.PICKUP));
            concesionaria.agregarVehiculo(new Motocicleta("Honda", "CB 250 Twister", 2024, "A123BCD", "Rojo", 0, 5200.0, TipoCarroceriaMoto.NAKED));
            concesionaria.agregarVehiculo(new Motocicleta("Yamaha", "XTZ 125", 2021, "A098XYZ", "Blanco", 18500, 3100.0, TipoCarroceriaMoto.ENDURO));
        } catch (DatoInvalidoException e) {
            System.err.println("Error al precargar inventario: " + e.getMessage());
        }
    }
}