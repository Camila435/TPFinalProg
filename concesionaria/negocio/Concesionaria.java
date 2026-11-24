package concesionaria.negocio;

import concesionaria.dominio.Auto;
import concesionaria.dominio.Camioneta;
import concesionaria.dominio.DatoInvalidoException;
import concesionaria.dominio.Estado;
import concesionaria.dominio.Motocicleta;
import concesionaria.dominio.PatenteInvalidaException;
import concesionaria.dominio.Vehiculo;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Capa de NEGOCIO (POJO): gestiona el inventario de ventas y el catálogo dinámico
 * de marcas y modelos de GyQ Concesionaria.
 *
 * @author GyQ Concesionaria
 * @version 1.1
 */
// Clase POJO pura de lógica de negocio: NO contiene ningún System.out.println ni Scanner;
// si falla una regla comercial, lanza excepciones para que la capa de presentación las maneje.
public class Concesionaria {

    // =========================================================================
    // 1. AMBIENTE DE ATRIBUTOS (ESTRUCTURAS DE DATOS EN MEMORIA)
    // =========================================================================
    // Map<Clave, Valor> con LinkedHashMap: garantiza patentes únicas (clave = patente),
    // búsqueda instantánea O(1) por patente y mantiene el orden en que fueron ingresados.
    private final Map<String, Vehiculo> inventario;

    // Diccionario dinámico: cada Marca (String) apunta a su lista de Modelos (List<String>)
    private final Map<String, List<String>> catalogoMarcas;

    // Acumulador monetario de las ventas concretadas en la sesión actual
    private Double totalRecaudadoVentas;

    // =========================================================================
    // 2. CONSTRUCTOR E INICIALIZACIÓN DE CATÁLOGO BASE
    // =========================================================================

    /**
     * Inicializa la concesionaria con inventario vacío y catálogo base precargado.
     */
    public Concesionaria() {
        this.inventario = new LinkedHashMap<>();
        this.catalogoMarcas = new LinkedHashMap<>();
        this.totalRecaudadoVentas = 0.0;
        inicializarCatalogoBase(); // Carga las marcas y modelos habituales al instanciar el negocio
    }

    /**
     * Precarga marcas y modelos habituales en el catálogo dinámico.
     */
    private void inicializarCatalogoBase() {
        registrarMarcaYModelo("Toyota", "Corolla");
        registrarMarcaYModelo("Toyota", "Hilux");
        registrarMarcaYModelo("Ford", "Focus");
        registrarMarcaYModelo("Ford", "Ranger");
        registrarMarcaYModelo("Volkswagen", "Amarok");
        registrarMarcaYModelo("Honda", "CB 250 Twister");
        registrarMarcaYModelo("Yamaha", "XTZ 125");
    }

    // =========================================================================
    // 3. GESTIÓN DEL CATÁLOGO DINÁMICO DE MARCAS Y MODELOS (MAP DE LISTAS)
    // =========================================================================

    /**
     * Agrega una nueva marca o un nuevo modelo a una marca existente en el catálogo.
     */
    public void registrarMarcaYModelo(String marca, String modelo) {
        if (marca == null || marca.trim().isEmpty()) return;
        String marcaLimpia = capitalizar(marca.trim()); // Estandariza ej: "fiat" -> "Fiat"

        // putIfAbsent: si la marca aún no existe en el Map, le crea una lista vacía; si ya existe, no la pisa
        catalogoMarcas.putIfAbsent(marcaLimpia, new ArrayList<>());

        if (modelo != null && !modelo.trim().isEmpty()) {
            String modeloLimpio = capitalizar(modelo.trim());
            List<String> modelos = catalogoMarcas.get(marcaLimpia);

            // Verifica sin distinguir mayúsculas/minúsculas que el modelo no esté duplicado
            boolean yaExiste = false;
            for (String m : modelos) {
                if (m.equalsIgnoreCase(modeloLimpio)) {
                    yaExiste = true;
                    break;
                }
            }
            if (!yaExiste) {
                modelos.add(modeloLimpio);
            }
        }
    }

    // Devuelve una vista inmodificable del Map (defensa de encapsulamiento para que nadie lo altere desde afuera)
    public Map<String, List<String>> getCatalogoMarcas() {
        return Collections.unmodifiableMap(catalogoMarcas);
    }

    // Devuelve una copia de las claves (nombres de marcas) registradas
    public List<String> getMarcasDisponibles() {
        return new ArrayList<>(catalogoMarcas.keySet());
    }

    // Devuelve una copia defensiva de los modelos asociados a una marca puntual
    public List<String> getModelosPorMarca(String marca) {
        List<String> modelos = catalogoMarcas.get(marca);
        return modelos != null ? new ArrayList<>(modelos) : new ArrayList<>();
    }

    // Método auxiliar interno: pone en mayúscula la primera letra y conserva el resto de la palabra
    private String capitalizar(String texto) {
        if (texto.isEmpty()) return texto;
        return texto.substring(0, 1).toUpperCase() + texto.substring(1);
    }

    // =========================================================================
    // 4. OPERACIONES CRUD DE INVENTARIO (ALTA, REEMPLAZO, BÚSQUEDA Y VENTA)
    // =========================================================================

    /**
     * Registra un nuevo vehículo en el inventario verificando que su patente no esté duplicada.
     */
    public void agregarVehiculo(Vehiculo vehiculo) throws DatoInvalidoException {
        if (vehiculo == null) {
            throw new DatoInvalidoException("El vehículo no puede ser nulo.");
        }
        String patenteClave = vehiculo.getPatente(); // Ya viene normalizada en mayúsculas y sin espacios

        // Regla de unicidad: si la patente ya está en el Map, rechaza el alta con PatenteInvalidaException
        if (inventario.containsKey(patenteClave)) {
            throw new PatenteInvalidaException("Ya existe un vehículo registrado con la patente " + patenteClave + ".");
        }

        inventario.put(patenteClave, vehiculo); // Guarda el vehículo usando la patente como clave única
        registrarMarcaYModelo(vehiculo.getMarca(), vehiculo.getModelo()); // Sincroniza el catálogo automáticamente
    }

    /**
     * Reemplaza el inventario en memoria con una lista cargada desde persistencia binaria.
     */
    public void reemplazarInventario(List<Vehiculo> listaCargada) throws DatoInvalidoException {
        if (listaCargada == null) {
            throw new DatoInvalidoException("La lista de vehículos a cargar no puede ser nula.");
        }
        inventario.clear(); // Vacía el stock anterior en RAM antes de volcar los objetos deserializados
        for (Vehiculo v : listaCargada) {
            agregarVehiculo(v); // Reutiliza agregarVehiculo() para validar que no vengan patentes repetidas
        }
    }

    // Consulta rápida O(1) normalizando previamente la patente ingresada por el usuario
    public boolean existePatente(String patente) {
        return inventario.containsKey(Vehiculo.normalizarPatente(patente));
    }

    // Recupera el objeto Vehiculo por su patente (o devuelve null si no se encuentra)
    public Vehiculo buscarPorPatente(String patente) {
        return inventario.get(Vehiculo.normalizarPatente(patente));
    }

    /**
     * Concreta la venta de un vehículo, lo elimina del stock y suma su precio final a la recaudación.
     */
    public double venderVehiculo(String patente) throws DatoInvalidoException {
        Vehiculo v = buscarPorPatente(patente);
        if (v == null) {
            throw new PatenteInvalidaException("No se encontró ningún vehículo en stock con la patente: " + patente);
        }
        // POLIMORFISMO PURO: invoca calcularPrecioFinal() de la interfaz Vendible; Java ejecuta
        // automáticamente la fórmula de Auto, Camioneta o Motocicleta según el tipo real del objeto.
        double precioFinal = v.calcularPrecioFinal();
        inventario.remove(v.getPatente());        // Da de baja el vehículo vendido del inventario
        this.totalRecaudadoVentas += precioFinal; // Acumula el ingreso en caja
        return precioFinal;
    }

    public Double getTotalRecaudadoVentas() {
        return totalRecaudadoVentas;
    }

    public int cantidadEnStock() {
        return inventario.size();
    }

    public boolean estaVacio() {
        return inventario.isEmpty();
    }

    // =========================================================================
    // 5. CONSULTAS, ORDENAMIENTO Y FILTRADO POLIMÓRFICO
    // =========================================================================

    // Devuelve una lista nueva con todos los valores del Map para evitar modificaciones externas directas
    public List<Vehiculo> getTodos() {
        return new ArrayList<>(inventario.values());
    }

    /**
     * Devuelve el inventario ordenado de forma descendente por año y ascendente por marca.
     */
    public List<Vehiculo> getInventarioOrdenadoPorAnio() {
        List<Vehiculo> lista = new ArrayList<>(inventario.values());
        // Usa Comparator encadenado: primero ordena del año más nuevo al más viejo (.reversed()),
        // y en caso de empate de año, desempata alfabéticamente por marca.
        lista.sort(Comparator.comparing(Vehiculo::getAnio).reversed()
                .thenComparing(Vehiculo::getMarca));
        return lista;
    }

    // Filtra usando 'instanceof' y downcasting seguro para devolver únicamente instancias de Auto
    public List<Auto> getAutos() {
        List<Auto> resultado = new ArrayList<>();
        for (Vehiculo v : inventario.values()) {
            if (v instanceof Auto) {
                resultado.add((Auto) v);
            }
        }
        return resultado;
    }

    // Filtra usando 'instanceof' y downcasting seguro para devolver únicamente instancias de Camioneta
    public List<Camioneta> getCamionetas() {
        List<Camioneta> resultado = new ArrayList<>();
        for (Vehiculo v : inventario.values()) {
            if (v instanceof Camioneta) {
                resultado.add((Camioneta) v);
            }
        }
        return resultado;
    }

    // Filtra usando 'instanceof' y downcasting seguro para devolver únicamente instancias de Motocicleta
    public List<Motocicleta> getMotos() {
        List<Motocicleta> resultado = new ArrayList<>();
        for (Vehiculo v : inventario.values()) {
            if (v instanceof Motocicleta) {
                resultado.add((Motocicleta) v);
            }
        }
        return resultado;
    }

    // Filtra comparando constantes del Enum Estado (NUEVO o USADO) con '=='
    public List<Vehiculo> getPorEstado(Estado estado) {
        List<Vehiculo> resultado = new ArrayList<>();
        for (Vehiculo v : inventario.values()) {
            if (v.getEstado() == estado) {
                resultado.add(v);
            }
        }
        return resultado;
    }
}