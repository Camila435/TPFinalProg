package concesionaria.dominio;

/**
 * Representa una motocicleta dentro de GyQ Concesionaria.
 * Cumple con el estándar JavaBean heredando {@link java.io.Serializable} desde {@link Vehiculo}.
 *
 * @author GyQ Concesionaria
 * @version 1.1
 */
// 'extends Vehiculo' aplica HERENCIA: Motocicleta reutiliza todos los atributos comunes (marca, modelo,
// año, patente, color, kilometraje, precioBase) y hereda las interfaces Vendible y Serializable.
public class Motocicleta extends Vehiculo {

    // Identificador de versión para la serialización binaria (.dat), exigido por Serializable
    private static final long serialVersionUID = 1L;

    // =========================================================================
    // 1. AMBIENTE DE ATRIBUTOS PROPIOS (ENCAPSULAMIENTO ESTRICTO + WRAPPERS)
    // =========================================================================
    // Atributos 'private' para impedir modificaciones directas sin validar:
    private Integer cilindrada;          // Cilindrada en cc (usa Wrapper Integer según estándar JavaBean)
    private TipoCarroceriaMoto tipoMoto; // Enum propio con los segmentos de moto (NAKED, ENDURO, SCOOTER, etc.)

    // =========================================================================
    // 2. CONSTRUCTORES (SOBRECARGA DE CONSTRUCTORES)
    // =========================================================================

    /**
     * Constructor vacío requerido por el estándar JavaBean.
     */
    public Motocicleta() {
        super(); // Invoca al constructor sin parámetros de la superclase Vehiculo
    }

    /**
     * Construye una instancia completa de motocicleta validando sus atributos.
     */
    public Motocicleta(String marca, String modelo, Integer anio, String patente,
                       String color, Integer kilometraje, Double precioBase,
                       Integer cilindrada, TipoCarroceriaMoto tipoMoto) throws DatoInvalidoException {
        // Delega a Vehiculo la inicialización y validación de los 7 datos comunes
        super(marca, modelo, anio, patente, color, kilometraje, precioBase);
        // Valida y asigna los 2 atributos propios de la motocicleta a través de sus setters
        setCilindrada(cilindrada);
        setTipoMoto(tipoMoto);
    }

    // =========================================================================
    // 3. MÉTODOS ACCESORES Y MUTADORES (GETTERS Y SETTERS CON VALIDACIÓN)
    // =========================================================================

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
    // Regla de dominio: ninguna motocicleta comercial puede tener menos de 50 cc ni valor nulo
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
    // Garantiza que toda motocicleta tenga asignada una constante válida de TipoCarroceriaMoto
    public void setTipoMoto(TipoCarroceriaMoto tipoMoto) throws DatoInvalidoException {
        if (tipoMoto == null) {
            throw new DatoInvalidoException("Debe seleccionar un tipo de motocicleta.");
        }
        this.tipoMoto = tipoMoto;
    }

    // =========================================================================
    // 4. MÉTODOS POLIMÓRFICOS SOBRESCRITOS (@Override)
    // =========================================================================

    // Implementa el método abstracto de Vehiculo identificando esta subclase como MOTOCICLETA
    @Override
    public TipoVehiculo getTipo() {
        return TipoVehiculo.MOTOCICLETA;
    }

    // Implementa el método abstracto de Vehiculo devolviendo el estilo de la moto (ej. "Naked", "Enduro")
    @Override
    public String getCarroceria() {
        return tipoMoto != null ? tipoMoto.toString() : "Sin definir";
    }

    // Implementa el contrato de Vendible: a diferencia de Auto y Camioneta (que calculan por 0 KM/Usado),
    // en Motocicleta el recargo comercial depende de la cilindrada: 8% si supera 250 cc, o 3% hasta 250 cc.
    @Override
    public double calcularPrecioFinal() {
        double recargo = (cilindrada > 250) ? 1.08 : 1.03;
        return getPrecioBase() * recargo;
    }

    // Redefine toString() reutilizando la cadena base de Vehiculo ('super.toString()')
    // y agregándole la cilindrada en cc y el precio final calculado.
    @Override
    public String toString() {
        return super.toString() + String.format(" | Cilindrada: %d cc | Precio Final: $%.2f",
                cilindrada, calcularPrecioFinal());
    }
}