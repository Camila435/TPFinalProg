package concesionaria.dominio;

import java.io.Serializable;
import java.time.Year;
import java.util.Objects;

/**
 * Superclase abstracta que representa la base de todo vehículo en GyQ Concesionaria.
 * <p>
 * Implementa el estándar <b>JavaBean</b> (constructor sin parámetros, atributos privados
 * encapsulados, getters/setters públicos e implementación de {@link Serializable} con
 * {@code serialVersionUID}) para permitir su persistencia en flujos binarios.
 * </p>
 *
 * @author Camila Quintero
 * @author Guadalupe Gallardo
 * @version 1.1
 */
public abstract class Vehiculo implements Vendible, Serializable {

    private static final long serialVersionUID = 1L;

    /** Año mínimo de fabricación admitido en el sistema. */
    public static final int ANIO_MINIMO = 1950;

    private String marca;
    private String modelo;
    private Integer anio;
    private String patente;
    private String color;
    private Integer kilometraje;
    private Double precioBase;

    /**
     * Constructor público sin parámetros requerido por el estándar JavaBean
     * y los mecanismos de deserialización.
     */
    public Vehiculo() {
    }

    /**
     * Constructor parametrizado que inicializa y valida todos los atributos comunes.
     *
     * @param marca       Fabricante del vehículo.
     * @param modelo      Denominación comercial del modelo.
     * @param anio        Año de fabricación.
     * @param patente     Dominio o matrícula identificatoria.
     * @param color       Color exterior de la unidad.
     * @param kilometraje Kilómetros recorridos (0 para unidades nuevas).
     * @param precioBase  Precio base de lista antes de impuestos.
     * @throws DatoInvalidoException Si alguno de los valores incumple las reglas de dominio.
     */
    public Vehiculo(String marca, String modelo, Integer anio, String patente,
                    String color, Integer kilometraje, Double precioBase) throws DatoInvalidoException {
        setMarca(marca);
        setModelo(modelo);
        setAnio(anio);
        setPatente(patente);
        setColor(color);
        setKilometraje(kilometraje);
        setPrecioBase(precioBase);
    }

    /**
     * Indica si el vehículo es 0 KM.
     *
     * @return {@code true} si el kilometraje es exactamente 0; {@code false} en caso contrario.
     */
    public boolean esNuevo() {
        return this.kilometraje != null && this.kilometraje.intValue() == 0;
    }

    /**
     * Obtiene el estado comercial ({@link Estado#NUEVO} o {@link Estado#USADO}).
     *
     * @return Estado correspondiente según el kilometraje.
     */
    public Estado getEstado() {
        return esNuevo() ? Estado.NUEVO : Estado.USADO;
    }

    /**
     * Calcula el año máximo permitido (año calendario actual + 1 para modelos adelantados).
     *
     * @return Año máximo válido.
     */
    public static int anioMaximo() {
        return Year.now().getValue() + 1;
    }

    /**
     * Limpia espacios, guiones y convierte a mayúsculas una cadena de patente.
     *
     * @param patente Cadena original ingresada.
     * @return Patente normalizada.
     */
    public static String normalizarPatente(String patente) {
        return patente == null ? "" : patente.trim().toUpperCase().replace(" ", "").replace("-", "");
    }

    /**
     * Verifica si una patente cumple con el formato argentino tradicional (ABC123)
     * o Mercosur (AB123CD / A123BCD).
     *
     * @param patente Patente a validar.
     * @return {@code true} si el formato es válido.
     */
    public static boolean esPatenteValida(String patente) {
        String p = normalizarPatente(patente);
        return p.matches("^[A-Z]{3}\\d{3}$")
                || p.matches("^[A-Z]{2}\\d{3}[A-Z]{2}$")
                || p.matches("^[A-Z]\\d{3}[A-Z]{3}$");
    }

    /**
     * Devuelve la categoría general del vehículo.
     *
     * @return Tipo de vehículo implementado por la subclase.
     */
    public abstract TipoVehiculo getTipo();

    /**
     * Devuelve la descripción textual de la carrocería o estilo específico.
     *
     * @return Nombre de la carrocería.
     */
    public abstract String getCarroceria();

    /**
     * Obtiene la marca del vehículo.
     *
     * @return Marca del fabricante.
     */
    public String getMarca() {
        return marca;
    }

    /**
     * Establece la marca del vehículo.
     *
     * @param marca Nombre de la marca.
     * @throws DatoInvalidoException Si la marca es nula o vacía.
     */
    public void setMarca(String marca) throws DatoInvalidoException {
        if (marca == null || marca.trim().isEmpty()) {
            throw new DatoInvalidoException("La marca no puede estar vacía.");
        }
        this.marca = marca.trim();
    }

    /**
     * Obtiene el modelo del vehículo.
     *
     * @return Nombre del modelo.
     */
    public String getModelo() {
        return modelo;
    }

    /**
     * Establece el modelo del vehículo.
     *
     * @param modelo Nombre del modelo.
     * @throws DatoInvalidoException Si el modelo es nulo o vacío.
     */
    public void setModelo(String modelo) throws DatoInvalidoException {
        if (modelo == null || modelo.trim().isEmpty()) {
            throw new DatoInvalidoException("El modelo no puede estar vacío.");
        }
        this.modelo = modelo.trim();
    }

    /**
     * Obtiene el año de fabricación.
     *
     * @return Año del vehículo.
     */
    public Integer getAnio() {
        return anio;
    }

    /**
     * Establece el año de fabricación validando el rango permitido.
     *
     * @param anio Año a asignar.
     * @throws DatoInvalidoException Si está fuera del rango [{@value #ANIO_MINIMO}..año actual + 1].
     */
    public void setAnio(Integer anio) throws DatoInvalidoException {
        if (anio == null || anio.intValue() < ANIO_MINIMO || anio.intValue() > anioMaximo()) {
            throw new DatoInvalidoException("El año debe estar entre " + ANIO_MINIMO + " y " + anioMaximo() + ".");
        }
        this.anio = anio;
    }

    /**
     * Obtiene la patente normalizada del vehículo.
     *
     * @return Dominio del vehículo.
     */
    public String getPatente() {
        return patente;
    }

    /**
     * Establece y valida la patente del vehículo.
     *
     * @param patente Patente a registrar.
     * @throws PatenteInvalidaException Si el formato de patente no es válido.
     */
    public void setPatente(String patente) throws PatenteInvalidaException {
        String normalizada = normalizarPatente(patente);
        if (!esPatenteValida(normalizada)) {
            throw new PatenteInvalidaException("La patente '" + patente + "' no tiene un formato válido (ej: ABC123 o AB123CD).");
        }
        this.patente = normalizada;
    }

    /**
     * Obtiene el color del vehículo.
     *
     * @return Color exterior.
     */
    public String getColor() {
        return color;
    }

    /**
     * Establece el color del vehículo.
     *
     * @param color Color a asignar.
     * @throws DatoInvalidoException Si el color es nulo o vacío.
     */
    public void setColor(String color) throws DatoInvalidoException {
        if (color == null || color.trim().isEmpty()) {
            throw new DatoInvalidoException("El color no puede estar vacío.");
        }
        this.color = color.trim();
    }

    /**
     * Obtiene el kilometraje actual del vehículo.
     *
     * @return Kilómetros recorridos.
     */
    public Integer getKilometraje() {
        return kilometraje;
    }

    /**
     * Establece el kilometraje del vehículo.
     *
     * @param kilometraje Kilómetros recorridos (debe ser mayor o igual a 0).
     * @throws DatoInvalidoException Si el kilometraje es nulo o negativo.
     */
    public void setKilometraje(Integer kilometraje) throws DatoInvalidoException {
        if (kilometraje == null || kilometraje.intValue() < 0) {
            throw new DatoInvalidoException("El kilometraje no puede ser negativo.");
        }
        this.kilometraje = kilometraje;
    }

    /**
     * Obtiene el precio base de lista.
     *
     * @return Precio base sin recargos.
     */
    public Double getPrecioBase() {
        return precioBase;
    }

    /**
     * Establece el precio base de lista.
     *
     * @param precioBase Importe base positivo.
     * @throws DatoInvalidoException Si el precio es nulo o menor o igual a cero.
     */
    public void setPrecioBase(Double precioBase) throws DatoInvalidoException {
        if (precioBase == null || precioBase.doubleValue() <= 0) {
            throw new DatoInvalidoException("El precio base debe ser mayor a $0.");
        }
        this.precioBase = precioBase;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vehiculo vehiculo = (Vehiculo) o;
        return Objects.equals(this.patente, vehiculo.patente);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.patente);
    }

    @Override
    public String toString() {
        return String.format("[%s] %s %s (%d) | Color: %s | Carrocería: %s | %s (%d km) | Precio Base: $%.2f",
                patente, marca, modelo, anio, color, getCarroceria(), getEstado(), kilometraje, precioBase);
    }
}