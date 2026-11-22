package concesionaria.dominio;

/**
 * Representa una motocicleta dentro de GyQ Concesionaria.
 * Cumple con el estándar JavaBean heredando {@link java.io.Serializable} desde {@link Vehiculo}.
 *
 * @author GyQ Concesionaria
 * @version 1.1
 */
public class Motocicleta extends Vehiculo {

    private static final long serialVersionUID = 1L;

    private Integer cilindrada;
    private TipoCarroceriaMoto tipoMoto;

    /**
     * Constructor vacío requerido por el estándar JavaBean.
     */
    public Motocicleta() {
        super();
    }

    /**
     * Construye una instancia completa de motocicleta validando sus atributos.
     *
     * @param marca       Marca del fabricante.
     * @param modelo      Modelo de la motocicleta.
     * @param anio        Año de fabricación.
     * @param patente     Patente identificatoria.
     * @param color       Color exterior.
     * @param kilometraje Kilometraje actual.
     * @param precioBase  Precio base de lista.
     * @param cilindrada  Cilindrada del motor en centímetros cúbicos (mínimo 50 cc).
     * @param tipoMoto    Estilo o segmento de la motocicleta.
     * @throws DatoInvalidoException Si algún parámetro no supera las validaciones.
     */
    public Motocicleta(String marca, String modelo, Integer anio, String patente,
                       String color, Integer kilometraje, Double precioBase,
                       Integer cilindrada, TipoCarroceriaMoto tipoMoto) throws DatoInvalidoException {
        super(marca, modelo, anio, patente, color, kilometraje, precioBase);
        setCilindrada(cilindrada);
        setTipoMoto(tipoMoto);
    }

    /**
     * Obtiene la cilindrada del motor en cc.
     *
     * @return Cilindrada.
     */
    public Integer getCilindrada() {
        return cilindrada;
    }

    /**
     * Establece la cilindrada validando el mínimo de 50 cc.
     *
     * @param cilindrada Cilindrada en cc.
     * @throws DatoInvalidoException Si es nula o menor a 50.
     */
    public void setCilindrada(Integer cilindrada) throws DatoInvalidoException {
        if (cilindrada == null || cilindrada.intValue() < 50) {
            throw new DatoInvalidoException("La cilindrada mínima válida es de 50 cc.");
        }
        this.cilindrada = cilindrada;
    }

    /**
     * Obtiene el estilo de la motocicleta.
     *
     * @return Constante de {@link TipoCarroceriaMoto}.
     */
    public TipoCarroceriaMoto getTipoMoto() {
        return tipoMoto;
    }

    /**
     * Establece el estilo de la motocicleta.
     *
     * @param tipoMoto Estilo a asignar.
     * @throws DatoInvalidoException Si es nulo.
     */
    public void setTipoMoto(TipoCarroceriaMoto tipoMoto) throws DatoInvalidoException {
        if (tipoMoto == null) {
            throw new DatoInvalidoException("Debe seleccionar un tipo de motocicleta.");
        }
        this.tipoMoto = tipoMoto;
    }

    @Override
    public TipoVehiculo getTipo() {
        return TipoVehiculo.MOTOCICLETA;
    }

    @Override
    public String getCarroceria() {
        return tipoMoto != null ? tipoMoto.toString() : "Sin definir";
    }

    @Override
    public double calcularPrecioFinal() {
        double recargo = (cilindrada > 250) ? 1.08 : 1.03;
        return getPrecioBase() * recargo;
    }

    @Override
    public String toString() {
        return super.toString() + String.format(" | Cilindrada: %d cc | Precio Final: $%.2f",
                cilindrada, calcularPrecioFinal());
    }
}