package negocio;

import concesionaria.dominio.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * CAPA DE NEGOCIO: administra el inventario, catálogo y ventas.
 * Separación de responsabilidades: no realiza entrada ni salida por consola.
 */
public class Concesionaria {
    // ENCAPSULAMIENTO: los atributos privados no se manipulan desde MainApp.
    // GENÉRICOS Y POLIMORFISMO: una patente identifica cualquier subtipo de Vehiculo.
    private final Map<String, Vehiculo> inventario;
    // COLECCIONES ANIDADAS: cada marca contiene una lista de modelos.
    private final Map<String, List<String>> catalogoMarcas;
    // WRAPPER: Double envuelve al tipo primitivo double.
    private Double totalRecaudadoVentas;

    /** CONSTRUCTOR: inicializa el estado de la instancia. */
    public Concesionaria() {
        this.inventario = new LinkedHashMap<>();
        this.catalogoMarcas = new LinkedHashMap<>();
        this.totalRecaudadoVentas = 0.0;
        inicializarCatalogoBase();
    }

    private void inicializarCatalogoBase() {
        registrarMarcaYModelo("Toyota", "Corolla");
        registrarMarcaYModelo("Toyota", "Hilux");
        registrarMarcaYModelo("Ford", "Focus");
        registrarMarcaYModelo("Ford", "Ranger");
        registrarMarcaYModelo("Volkswagen", "Amarok");
        registrarMarcaYModelo("Honda", "CB 250 Twister");
        registrarMarcaYModelo("Yamaha", "XTZ 125");
    }

    /**
     * Registra una marca y modelo sin repetir el modelo en una misma marca.
     * putIfAbsent incorpora una lista solo si no existe la clave.
     * @param marca nombre de la marca
     * @param modelo nombre del modelo
     */
    public void registrarMarcaYModelo(String marca, String modelo) {
        if (marca == null || marca.trim().isEmpty()) return;
        String marcaLimpia = capitalizar(marca.trim());
        catalogoMarcas.putIfAbsent(marcaLimpia, new ArrayList<>());
        if (modelo != null && !modelo.trim().isEmpty()) {
            String modeloLimpio = capitalizar(modelo.trim());
            List<String> modelos = catalogoMarcas.get(marcaLimpia);
            boolean yaExiste = false;
            // FOR-EACH recorre la colección y break interrumpe la búsqueda.
            for (String m : modelos) {
                if (m.equalsIgnoreCase(modeloLimpio)) {
                    yaExiste = true;
                    break;
                }
            }
            if (!yaExiste) modelos.add(modeloLimpio);
        }
    }

    /**
     * Vista del catálogo que no permite modificar directamente el mapa.
     * Las listas internas siguen siendo mutables: no es inmutabilidad profunda.
     */
    public Map<String, List<String>> getCatalogoMarcas() {
        return Collections.unmodifiableMap(catalogoMarcas);
    }

    /** COPIA DEFENSIVA: devuelve una lista nueva de marcas. */
    public List<String> getMarcasDisponibles() {
        return new ArrayList<>(catalogoMarcas.keySet());
    }

    /** OPERADOR TERNARIO: devuelve modelos o una lista vacía. */
    public List<String> getModelosPorMarca(String marca) {
        List<String> modelos = catalogoMarcas.get(marca);
        return modelos != null ? new ArrayList<>(modelos) : new ArrayList<>();
    }

    private String capitalizar(String texto) {
        if (texto.isEmpty()) return texto;
        return texto.substring(0, 1).toUpperCase() + texto.substring(1);
    }

    /**
     * Agrega un vehículo si su patente no está repetida.
     * POLIMORFISMO: acepta cualquier clase hija de Vehiculo.
     * @throws DatoInvalidoException cuando el vehículo es nulo o la patente se repite
     */
    public void agregarVehiculo(Vehiculo vehiculo) throws DatoInvalidoException {
        if (vehiculo == null) throw new DatoInvalidoException("El vehículo no puede ser nulo.");
        String patenteClave = vehiculo.getPatente();
        if (inventario.containsKey(patenteClave)) {
            throw new PatenteInvalidaException("Ya existe un vehículo registrado con la patente " + patenteClave + ".");
        }
        inventario.put(patenteClave, vehiculo);
        registrarMarcaYModelo(vehiculo.getMarca(), vehiculo.getModelo());
    }

    /** CONSULTA DE MAPA: containsKey devuelve un booleano. */
    public boolean existePatente(String patente) {
        return inventario.containsKey(Vehiculo.normalizarPatente(patente));
    }

    public Vehiculo buscarPorPatente(String patente) {
        return inventario.get(Vehiculo.normalizarPatente(patente));
    }

    /**
     * Registra una venta, quita el vehículo del stock y suma lo recaudado.
     * DESPACHO DINÁMICO: se ejecuta calcularPrecioFinal del subtipo concreto.
     * @return precio final de venta
     * @throws DatoInvalidoException si la patente no está en el inventario
     */
    public double venderVehiculo(String patente) throws DatoInvalidoException {
        Vehiculo v = buscarPorPatente(patente);
        if (v == null) throw new PatenteInvalidaException("No se encontró ningún vehículo en stock con la patente: " + patente);
        double precioFinal = v.calcularPrecioFinal();
        inventario.remove(v.getPatente());
        // AUTOBOXING/UNBOXING: suma un double a un atributo Double.
        this.totalRecaudadoVentas += precioFinal;
        return precioFinal;
    }

    public Double getTotalRecaudadoVentas() { return totalRecaudadoVentas; }
    public int cantidadEnStock() { return inventario.size(); }
    public boolean estaVacio() { return inventario.isEmpty(); }

    /** Devuelve una copia de la lista, no de cada vehículo (copia superficial). */
    public List<Vehiculo> getTodos() {
        return new ArrayList<>(inventario.values());
    }

    /** COMPARATOR y REFERENCIAS A MÉTODOS: ordena por año descendente y marca. */
    public List<Vehiculo> getInventarioOrdenadoPorAnio() {
        List<Vehiculo> lista = new ArrayList<>(inventario.values());
        lista.sort(Comparator.comparing(Vehiculo::getAnio).reversed()
                .thenComparing(Vehiculo::getMarca));
        return lista;
    }

    /** INSTANCEOF y CAST: selecciona objetos Automovil de la colección polimórfica. */
    public List<Automovil> getAutos() {
        List<Automovil> resultado = new ArrayList<>();
        for (Vehiculo v : inventario.values()) {
            if (v instanceof Automovil) resultado.add((Automovil) v);
        }
        return resultado;
    }

    public List<Camioneta> getCamionetas() {
        List<Camioneta> resultado = new ArrayList<>();
        for (Vehiculo v : inventario.values()) {
            if (v instanceof Camioneta) resultado.add((Camioneta) v);
        }
        return resultado;
    }

    public List<Motocicleta> getMotos() {
        List<Motocicleta> resultado = new ArrayList<>();
        for (Vehiculo v : inventario.values()) {
            if (v instanceof Motocicleta) resultado.add((Motocicleta) v);
        }
        return resultado;
    }

    /** ENUM: compara constantes de Estado mediante ==. */
    public List<Vehiculo> getPorEstado(Estado estado) {
        List<Vehiculo> resultado = new ArrayList<>();
        for (Vehiculo v : inventario.values()) {
            if (v.getEstado() == estado) resultado.add(v);
        }
        return resultado;
    }
}
