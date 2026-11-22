package concesionaria.dominio;

/**
 * Representa un automóvil dentro del inventario de ventas de GyQ Concesionaria.
 * Cumple con el estándar JavaBean heredando {@link java.io.Serializable} desde {@link Vehiculo}.
 *
 * @author GyQ Concesionaria
 * @version 1.1
 */
public class Auto extends Vehiculo {

    private static final long serialVersionUID = 1L;

    private Integer cantPuertas;
    private TipoCarroceria tipoCarroceria;

    /**
     * Constructor vacío requerido por el estándar JavaBean.
     */
    public Auto() {
        super();
    }

    /**
     * Construye una instancia completa de automóvil validando todos sus atributos.
     *
     * @param marca          Marca del fabricante.
     * @param modelo         Modelo del automóvil.
     * @param anio           Año de fabricación.
     * @param patente        Patente identificatoria.
     * @param color          Color exterior.
     * @param kilometraje    Kilometraje actual.
     * @param precioBase     Precio base de lista.
     * @param cantPuertas    Cantidad de puertas (entre 3 y 5).
     * @param tipoCarroceria Tipo de carrocería válida para automóvil.
     * @throws DatoInvalidoException Si algún dato incumple las reglas del dominio.
     */
    public Auto(String marca, String modelo, Integer anio, String patente,
                String color, Integer kilometraje, Double precioBase,
                Integer cantPuertas, TipoCarroceria tipoCarroceria) throws DatoInvalidoException {
        super(marca, modelo, anio, patente, color, kilometraje, precioBase);
        setCantPuertas(cantPuertas);
        setTipoCarroceria(tipoCarroceria);
    }

    /**
     * Obtiene la cantidad de puertas del automóvil.
     *
     * @return Número de puertas.
     */
    public Integer getCantPuertas() {
        return cantPuertas;
    }

    /**
     * Establece la cantidad de puertas validando el rango entre 3 y 5.
     *
     * @param cantPuertas Cantidad de puertas.
     * @throws PuertasInsuficientesException Si es menor a 3 o mayor a 5.
     */
    public void setCantPuertas(Integer cantPuertas) throws PuertasInsuficientesException {
        if (cantPuertas == null || cantPuertas.intValue() < 3 || cantPuertas.intValue() > 5) {
            throw new PuertasInsuficientesException("Un automóvil debe tener entre 3 y 5 puertas.");
        }
        this.cantPuertas = cantPuertas;
    }

    /**
     * Obtiene el tipo de carrocería del automóvil.
     *
     * @return Constante de {@link TipoCarroceria}.
     */
    public TipoCarroceria getTipoCarroceria() {
        return tipoCarroceria;
    }

    /**
     * Establece el tipo de carrocería verificando que corresponda a un automóvil.
     *
     * @param tipoCarroceria Carrocería a asignar.
     * @throws DatoInvalidoException Si es nula o pertenece a camionetas.
     */
    public void setTipoCarroceria(TipoCarroceria tipoCarroceria) throws DatoInvalidoException {
        if (tipoCarroceria == null || tipoCarroceria == TipoCarroceria.SUV
                || tipoCarroceria == TipoCarroceria.PICKUP || tipoCarroceria == TipoCarroceria.FURGON) {
            throw new DatoInvalidoException("La carrocería '" + tipoCarroceria + "' no corresponde a un automóvil.");
        }
        this.tipoCarroceria = tipoCarroceria;
    }

    @Override
    public TipoVehiculo getTipo() {
        return TipoVehiculo.AUTOMOVIL;
    }

    @Override
    public String getCarroceria() {
        return tipoCarroceria != null ? tipoCarroceria.toString() : "Sin definir";
    }

    @Override
    public double calcularPrecioFinal() {
        double recargo = esNuevo() ? 1.10 : 1.04;
        return getPrecioBase() * recargo;
    }

    @Override
    public String toString() {
        return super.toString() + String.format(" | Puertas: %d | Precio Final: $%.2f",
                cantPuertas, calcularPrecioFinal());
    }
}