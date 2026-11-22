package concesionaria.dominio;

/**
 * Representa una camioneta (SUV, Pick-up o Furgón) en GyQ Concesionaria.
 * Cumple con el estándar JavaBean heredando {@link java.io.Serializable} desde {@link Vehiculo}.
 *
 * @author GyQ Concesionaria
 * @version 1.1
 */
public class Camioneta extends Vehiculo {

    private static final long serialVersionUID = 1L;

    private TipoCarroceria tipoCarroceria;
    private Double capacidadCargaKg;
    private Boolean tiene4x4;

    /**
     * Constructor vacío requerido por el estándar JavaBean.
     */
    public Camioneta() {
        super();
    }

    /**
     * Construye una instancia completa de camioneta validando sus atributos.
     *
     * @param marca            Marca del fabricante.
     * @param modelo           Modelo de la camioneta.
     * @param anio             Año de fabricación.
     * @param patente          Patente identificatoria.
     * @param color            Color exterior.
     * @param kilometraje      Kilometraje actual.
     * @param precioBase       Precio base de lista.
     * @param tipoCarroceria   Carrocería válida para camioneta (SUV, Pick-up o Furgón).
     * @param capacidadCargaKg Capacidad máxima de carga en kilogramos.
     * @param tiene4x4         Indica si posee tracción integral 4x4.
     * @throws DatoInvalidoException Si algún parámetro no supera las validaciones.
     */
    public Camioneta(String marca, String modelo, Integer anio, String patente,
                     String color, Integer kilometraje, Double precioBase,
                     TipoCarroceria tipoCarroceria, Double capacidadCargaKg, Boolean tiene4x4) throws DatoInvalidoException {
        super(marca, modelo, anio, patente, color, kilometraje, precioBase);
        setTipoCarroceria(tipoCarroceria);
        setCapacidadCargaKg(capacidadCargaKg);
        setTiene4x4(tiene4x4);
    }

    /**
     * Obtiene el tipo de carrocería de la camioneta.
     *
     * @return Constante de {@link TipoCarroceria}.
     */
    public TipoCarroceria getTipoCarroceria() {
        return tipoCarroceria;
    }

    /**
     * Establece la carrocería verificando que corresponda a camioneta.
     *
     * @param tipoCarroceria Carrocería a asignar.
     * @throws DatoInvalidoException Si no es SUV, PICKUP o FURGON.
     */
    public void setTipoCarroceria(TipoCarroceria tipoCarroceria) throws DatoInvalidoException {
        if (tipoCarroceria != TipoCarroceria.SUV && tipoCarroceria != TipoCarroceria.PICKUP
                && tipoCarroceria != TipoCarroceria.FURGON) {
            throw new DatoInvalidoException("La carrocería '" + tipoCarroceria + "' no corresponde a una camioneta.");
        }
        this.tipoCarroceria = tipoCarroceria;
    }

    /**
     * Obtiene la capacidad de carga en kilogramos.
     *
     * @return Capacidad de carga.
     */
    public Double getCapacidadCargaKg() {
        return capacidadCargaKg;
    }

    /**
     * Establece la capacidad de carga validando que sea positiva.
     *
     * @param capacidadCargaKg Carga máxima en kg.
     * @throws DatoInvalidoException Si es nula o menor o igual a 0.
     */
    public void setCapacidadCargaKg(Double capacidadCargaKg) throws DatoInvalidoException {
        if (capacidadCargaKg == null || capacidadCargaKg.doubleValue() <= 0) {
            throw new DatoInvalidoException("La capacidad de carga debe ser mayor a 0 kg.");
        }
        this.capacidadCargaKg = capacidadCargaKg;
    }

    /**
     * Indica si la unidad cuenta con tracción 4x4.
     *
     * @return {@code true} si posee tracción 4x4.
     */
    public Boolean getTiene4x4() {
        return tiene4x4;
    }

    /**
     * Establece si posee tracción integral 4x4.
     *
     * @param tiene4x4 Valor booleano de tracción.
     */
    public void setTiene4x4(Boolean tiene4x4) {
        this.tiene4x4 = (tiene4x4 != null) ? tiene4x4 : Boolean.FALSE;
    }

    @Override
    public TipoVehiculo getTipo() {
        return TipoVehiculo.CAMIONETA;
    }

    @Override
    public String getCarroceria() {
        return tipoCarroceria != null ? tipoCarroceria.toString() : "Sin definir";
    }

    @Override
    public double calcularPrecioFinal() {
        double factor = esNuevo() ? 1.12 : 1.05;
        double extra4x4 = Boolean.TRUE.equals(tiene4x4) ? 1500.0 : 0.0;
        return (getPrecioBase() * factor) + extra4x4;
    }

    @Override
    public String toString() {
        String traccion = Boolean.TRUE.equals(tiene4x4) ? "4x4" : "4x2";
        return super.toString() + String.format(" | Carga: %.0f kg | Tracción: %s | Precio Final: $%.2f",
                capacidadCargaKg, traccion, calcularPrecioFinal());
    }
}