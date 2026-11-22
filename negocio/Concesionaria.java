package negocio;

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
public class Concesionaria {

    private final Map<String, Vehiculo> inventario;
    private final Map<String, List<String>> catalogoMarcas;
    private Double totalRecaudadoVentas;

    /**
     * Inicializa la concesionaria con inventario vacío y catálogo base precargado.
     */
    public Concesionaria() {
        this.inventario = new LinkedHashMap<>();
        this.catalogoMarcas = new LinkedHashMap<>();
        this.totalRecaudadoVentas = 0.0;
        inicializarCatalogoBase();
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

    /**
     * Agrega una nueva marca o un nuevo modelo a una marca existente en el catálogo.
     *
     * @param marca  Nombre de la marca.
     * @param modelo Nombre del modelo asociado.
     */
    public void registrarMarcaYModelo(String marca, String modelo) {
        if (marca == null || marca.trim().isEmpty()) return;
        String marcaLimpia = capitalizar(marca.trim());

        catalogoMarcas.putIfAbsent(marcaLimpia, new ArrayList<>());

        if (modelo != null && !modelo.trim().isEmpty()) {
            String modeloLimpio = capitalizar(modelo.trim());
            List<String> modelos = catalogoMarcas.get(marcaLimpia);
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

    /**
     * Devuelve una vista de solo lectura del catálogo completo de marcas y modelos.
     *
     * @return Mapa inmodificable con marcas como clave y listas de modelos como valor.
     */
    public Map<String, List<String>> getCatalogoMarcas() {
        return Collections.unmodifiableMap(catalogoMarcas);
    }

    /**
     * Obtiene la lista de marcas registradas actualmente.
     *
     * @return Lista de nombres de marcas.
     */
    public List<String> getMarcasDisponibles() {
        return new ArrayList<>(catalogoMarcas.keySet());
    }

    /**
     * Obtiene los modelos disponibles para una marca específica.
     *
     * @param marca Marca consultada.
     * @return Lista de modelos registrados para esa marca.
     */
    public List<String> getModelosPorMarca(String marca) {
        List<String> modelos = catalogoMarcas.get(marca);
        return modelos != null ? new ArrayList<>(modelos) : new ArrayList<>();
    }

    /**
     * Convierte a mayúscula la primera letra de una cadena.
     *
     * @param texto Cadena de entrada.
     * @return Cadena capitalizada.
     */
    private String capitalizar(String texto) {
        if (texto.isEmpty()) return texto;
        return texto.substring(0, 1).toUpperCase() + texto.substring(1);
    }

    /**
     * Registra un nuevo vehículo en el inventario verificando que su patente no esté duplicada.
     *
     * @param vehiculo Instancia de vehículo a incorporar.
     * @throws DatoInvalidoException Si el vehículo es nulo o la patente ya existe.
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
        registrarMarcaYModelo(vehiculo.getMarca(), vehiculo.getModelo());
    }

    /**
     * Reemplaza el inventario en memoria con una lista cargada desde persistencia binaria.
     *
     * @param listaCargada Lista de vehículos deserializados.
     * @throws DatoInvalidoException Si alguna unidad presenta inconsistencias.
     */
    public void reemplazarInventario(List<Vehiculo> listaCargada) throws DatoInvalidoException {
        if (listaCargada == null) {
            throw new DatoInvalidoException("La lista de vehículos a cargar no puede ser nula.");
        }
        inventario.clear();
        for (Vehiculo v : listaCargada) {
            agregarVehiculo(v);
        }
    }

    /**
     * Comprueba si una patente ya se encuentra registrada en stock.
     *
     * @param patente Patente a verificar.
     * @return {@code true} si existe en el inventario.
     */
    public boolean existePatente(String patente) {
        return inventario.containsKey(Vehiculo.normalizarPatente(patente));
    }

    /**
     * Busca un vehículo en el inventario por su patente.
     *
     * @param patente Patente buscada.
     * @return El vehículo encontrado, o {@code null} si no existe.
     */
    public Vehiculo buscarPorPatente(String patente) {
        return inventario.get(Vehiculo.normalizarPatente(patente));
    }

    /**
     * Concreta la venta de un vehículo, lo elimina del stock y suma su precio final a la recaudación.
     *
     * @param patente Patente de la unidad vendida.
     * @return Precio final cobrado al cliente.
     * @throws DatoInvalidoException Si no existe un vehículo con esa patente.
     */
    public double venderVehiculo(String patente) throws DatoInvalidoException {
        Vehiculo v = buscarPorPatente(patente);
        if (v == null) {
            throw new PatenteInvalidaException("No se encontró ningún vehículo en stock con la patente: " + patente);
        }
        double precioFinal = v.calcularPrecioFinal();
        inventario.remove(v.getPatente());
        this.totalRecaudadoVentas += precioFinal;
        return precioFinal;
    }

    /**
     * Obtiene el monto total acumulado por ventas realizadas.
     *
     * @return Total recaudado.
     */
    public Double getTotalRecaudadoVentas() {
        return totalRecaudadoVentas;
    }

    /**
     * Devuelve la cantidad de vehículos actualmente disponibles en stock.
     *
     * @return Número de unidades en inventario.
     */
    public int cantidadEnStock() {
        return inventario.size();
    }

    /**
     * Indica si el inventario no posee unidades.
     *
     * @return {@code true} si está vacío.
     */
    public boolean estaVacio() {
        return inventario.isEmpty();
    }

    /**
     * Devuelve todos los vehículos en stock en orden de inserción.
     *
     * @return Lista con todos los vehículos.
     */
    public List<Vehiculo> getTodos() {
        return new ArrayList<>(inventario.values());
    }

    /**
     * Devuelve el inventario ordenado de forma descendente por año y ascendente por marca.
     *
     * @return Lista ordenada de vehículos.
     */
    public List<Vehiculo> getInventarioOrdenadoPorAnio() {
        List<Vehiculo> lista = new ArrayList<>(inventario.values());
        lista.sort(Comparator.comparing(Vehiculo::getAnio).reversed()
                .thenComparing(Vehiculo::getMarca));
        return lista;
    }

    /**
     * Filtra y devuelve únicamente los automóviles en stock.
     *
     * @return Lista de instancias de {@link Auto}.
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

    /**
     * Filtra y devuelve únicamente las camionetas en stock.
     *
     * @return Lista de instancias de {@link Camioneta}.
     */
    public List<Camioneta> getCamionetas() {
        List<Camioneta> resultado = new ArrayList<>();
        for (Vehiculo v : inventario.values()) {
            if (v instanceof Camioneta) {
                resultado.add((Camioneta) v);
            }
        }
        return resultado;
    }

    /**
     * Filtra y devuelve únicamente las motocicletas en stock.
     *
     * @return Lista de instancias de {@link Motocicleta}.
     */
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
     * Filtra los vehículos según su estado comercial (0 KM o Usado).
     *
     * @param estado Estado comercial requerido.
     * @return Lista de vehículos que coinciden con el estado.
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