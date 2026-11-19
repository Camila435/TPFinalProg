package negocio;

import concesionaria.dominio.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * CAPA DE NEGOCIO: contiene las reglas del inventario, el catálogo y las ventas.
 * TEORÍA: separación de responsabilidades. La interfaz de consola no administra
 * directamente los datos, sino que invoca los métodos públicos de esta clase.
 */
public class Concesionaria {

    // ENCAPSULAMIENTO: private impide el acceso directo desde otras clases.
    // GENÉRICOS: Map<String, Vehiculo> asocia una patente con un vehículo.
    // POLIMORFISMO: el valor puede ser un Auto, Camioneta o Motocicleta.
    // LinkedHashMap mantiene el orden de inserción de las claves.
    private final Map<String, Vehiculo> inventario;

    // COLECCIONES ANIDADAS: cada marca tiene una lista dinámica de modelos.
    private final Map<String, List<String>> catalogoMarcas;
    // Double es una clase envoltorio (wrapper) del tipo primitivo double.
    private Double totalRecaudadoVentas;

    /** CONSTRUCTOR: inicializa los atributos cuando se crea una Concesionaria. */
    public Concesionaria() {
        // El operador diamante <> permite inferir los tipos genéricos.
        this.inventario = new LinkedHashMap<>();
        this.catalogoMarcas = new LinkedHashMap<>();
        this.totalRecaudadoVentas = 0.0;
        inicializarCatalogoBase();
    }

    // MÉTODO PRIVADO: detalle interno de inicialización que reutiliza otro método.
    private void inicializarCatalogoBase() {
        registrarMarcaYModelo("Toyota", "Corolla");
        registrarMarcaYModelo("Toyota", "Hilux");
        registrarMarcaYModelo("Ford", "Focus");
        registrarMarcaYModelo("Ford", "Ranger");
        registrarMarcaYModelo("Volkswagen", "Amarok");
        registrarMarcaYModelo("Honda", "CB 250 Twister");
        registrarMarcaYModelo("Yamaha", "XTZ 125");
    }

    // --- GESTIÓN DEL CATÁLOGO ---

    /**
     * COLECCIONES Y VALIDACIONES: agrega marcas y modelos sin repetir modelos.
     * putIfAbsent crea la lista únicamente cuando la marca no existe.
     * equalsIgnoreCase compara cadenas sin distinguir mayúsculas de minúsculas.
     */
    public void registrarMarcaYModelo(String marca, String modelo) {
        // GUARD CLAUSE: return interrumpe el método ante un dato inválido.
        if (marca == null || marca.trim().isEmpty()) return;
        String marcaLimpia = capitalizar(marca.trim());
        catalogoMarcas.putIfAbsent(marcaLimpia, new ArrayList<>());

        if (modelo != null && !modelo.trim().isEmpty()) {
            String modeloLimpio = capitalizar(modelo.trim());
            List<String> modelos = catalogoMarcas.get(marcaLimpia);
            boolean yaExiste = false;
            // FOR-EACH: recorre todos los elementos de la colección.
            for (String m : modelos) {
                if (m.equalsIgnoreCase(modeloLimpio)) {
                    yaExiste = true;
                    break; // Sale del ciclo cuando encuentra el modelo.
                }
            }
            if (!yaExiste) {
                modelos.add(modeloLimpio);
            }
        }
    }

    /**
     * VISTA NO MODIFICABLE: evita alterar directamente el mapa externo.
     * ATENCIÓN: las listas internas siguen siendo modificables por quien las reciba.
     */
    public Map<String, List<String>> getCatalogoMarcas() {
        return Collections.unmodifiableMap(catalogoMarcas);
    }

    // COPIA DEFENSIVA: la lista devuelta es independiente de las claves del mapa.
    public List<String> getMarcasDisponibles() {
        return new ArrayList<>(catalogoMarcas.keySet());
    }

    // OPERADOR TERNARIO: condición ? valorSiVerdadero : valorSiFalso.
    public List<String> getModelosPorMarca(String marca) {
        List<String> modelos = catalogoMarcas.get(marca);
        return modelos != null ? new ArrayList<>(modelos) : new ArrayList<>();
    }

    // ABSTRACCIÓN: concentra una operación de texto reutilizable.
    private String capitalizar(String texto) {
        if (texto.isEmpty()) return texto;
        return texto.substring(0, 1).toUpperCase() + texto.substring(1);
    }

    // --- INVENTARIO Y VENTAS ---

    /**
     * POLIMORFISMO POR HERENCIA: recibe Vehiculo, no una clase concreta.
     * EXCEPCIONES: throws declara la posible excepción; throw la lanza.
     * REGLA DE NEGOCIO: no se permiten patentes duplicadas.
     */
    public void agregarVehiculo(Vehiculo vehiculo) throws DatoInvalidoException {
        if (vehiculo == null) {
            throw new DatoInvalidoException("El vehículo no puede ser nulo.");
        }
        String patenteClave = vehiculo.getPatente();
        if (inventario.containsKey(patenteClave)) {
            throw new PatenteInvalidaException("Ya existe un vehículo registrado con la patente " + patenteClave + ".");
        }
        inventario.put(patenteClave, vehiculo);
        // SINCRONIZACIÓN: registra también la marca y modelo del vehículo nuevo.
        registrarMarcaYModelo(vehiculo.getMarca(), vehiculo.getModelo());
    }

    // BÚSQUEDA POR CLAVE: containsKey devuelve un booleano.
    public boolean existePatente(String patente) {
        return inventario.containsKey(Vehiculo.normalizarPatente(patente));
    }

    // Map.get devuelve el vehículo asociado o null si no existe.
    public Vehiculo buscarPorPatente(String patente) {
        return inventario.get(Vehiculo.normalizarPatente(patente));
    }

    /**
     * BAJA POR VENTA: localiza, calcula precio, retira y acumula recaudación.
     * DESPACHO DINÁMICO: calcularPrecioFinal() utiliza la implementación
     * correspondiente al tipo real del vehículo, si está sobrescrita.
     */
    public double venderVehiculo(String patente) throws DatoInvalidoException {
        Vehiculo v = buscarPorPatente(patente);
        if (v == null) {
            throw new PatenteInvalidaException("No se encontró ningún vehículo en stock con la patente: " + patente);
        }
        double precioFinal = v.calcularPrecioFinal();
        inventario.remove(v.getPatente());
        // AUTOBOXING / UNBOXING: Java convierte entre Double y double.
        this.totalRecaudadoVentas += precioFinal;
        return precioFinal;
    }

    // GETTER: consulta un atributo privado sin exponer el campo directamente.
    public Double getTotalRecaudadoVentas() {
        return totalRecaudadoVentas;
    }

    // size() cuenta las entradas del mapa.
    public int cantidadEnStock() {
        return inventario.size();
    }

    // isEmpty() devuelve true si no hay entradas.
    public boolean estaVacio() {
        return inventario.isEmpty();
    }

    // --- CONSULTAS Y FILTROS ---

    // COPIA SUPERFICIAL: nueva lista, pero referencias a los mismos vehículos.
    public List<Vehiculo> getTodos() {
        return new ArrayList<>(inventario.values());
    }

    /**
     * COMPARATOR: define un criterio de ordenamiento.
     * REFERENCIA A MÉTODO: Vehiculo::getAnio equivale a consultar su año.
     * reversed() invierte el orden; thenComparing desempata por marca.
     */
    public List<Vehiculo> getInventarioOrdenadoPorAnio() {
        List<Vehiculo> lista = new ArrayList<>(inventario.values());
        lista.sort(Comparator.comparing(Vehiculo::getAnio).reversed()
                .thenComparing(Vehiculo::getMarca));
        return lista;
    }

    /**
     * instanceof verifica el tipo real del objeto en ejecución.
     * CASTING: (Auto) convierte una referencia Vehiculo al subtipo Auto.
     * La comprobación previa evita un cast inválido.
     */
    public List<Auto> getAutos() {
        List<Auto> resultado = new ArrayList<>();
        for (Vehiculo v : inventario.values()) {
            if (v instanceof Auto) {
                resultado.add((Auto) v);
            }
        }
        return resultado;
    }

    // FILTRO POR SUBTIPO: se seleccionan únicamente camionetas.
    public List<Camioneta> getCamionetas() {
        List<Camioneta> resultado = new ArrayList<>();
        for (Vehiculo v : inventario.values()) {
            if (v instanceof Camioneta) {
                resultado.add((Camioneta) v);
            }
        }
        return resultado;
    }

    // GENÉRICOS: List<Motocicleta> restringe el tipo de sus elementos.
    public List<Motocicleta> getMotos() {
        List<Motocicleta> resultado = new ArrayList<>();
        for (Vehiculo v : inventario.values()) {
            if (v instanceof Motocicleta) {
                resultado.add((Motocicleta) v);
            }
        }
        return resultado;
    }

    /**
     * ENUM: Estado representa un conjunto cerrado de constantes.
     * Las constantes de un enum se pueden comparar correctamente con ==.
     */
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
