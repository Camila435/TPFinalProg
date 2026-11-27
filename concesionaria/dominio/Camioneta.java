package concesionaria.dominio;

/**
 * Clase genérica acotada (<b>POJO</b> de servicio) encargada de gestionar el lavado
 * obligatorio y el mantenimiento preventivo condicional de los vehículos de la concesionaria.
 * <p>
 * Utiliza el parámetro de tipo {@code <T extends Vehiculo>} para garantizar seguridad de tipos
 * en tiempo de compilación (<i>type-safety</i>) y permitir tanto talleres multimarca generales
 * como talleres especializados por subclase.
 * </p>
 *
 * @param <T> Tipo de vehículo que extiende de {@link Vehiculo}.
 * @author Camila Quintero
 * @author Guadalupe Gallardo
 * @version 1.2
 */
// 'extends Vehiculo' aplica HERENCIA: Camioneta hereda los atributos, validaciones y contratos
// (Vendible y Serializable) definidos en la superclase abstracta Vehiculo.
public class Camioneta extends Vehiculo {

    // Control de versión para la serialización binaria (.dat), exigido por Serializable
    private static final long serialVersionUID = 1L;

    // =========================================================================
    // 1. AMBIENTE DE ATRIBUTOS PROPIOS (ENCAPSULAMIENTO ESTRICTO + WRAPPERS)
    // =========================================================================
    // Atributos 'private' usando clases Wrapper (Double, Boolean) según la convención JavaBean:
    private TipoCarroceria tipoCarroceria;  // Constante del Enum (PICKUP, SUV, FURGON)
    private String carroceriaPersonalizada; // Texto libre si el operador elige "Otra" en el menú
    private Double capacidadCargaKg;        // Capacidad máxima de carga en kilogramos (debe ser > 0)
    private Boolean traccion4x4;            // Indica si posee doble tracción (true = 4x4, false = 4x2)

    // =========================================================================
    // 2. CONSTRUCTORES (SOBRECARGA DE CONSTRUCTORES)
    // =========================================================================

    // Constructor vacío obligatorio para cumplir el estándar JavaBean
    public Camioneta() {
        super(); // Invoca al constructor sin argumentos de Vehiculo
    }

    // Constructor principal cuando la carrocería proviene del Enum TipoCarroceria
    public Camioneta(String marca, String modelo, Integer anio, String patente, String color,
                     Integer kilometraje, Double precioBase, TipoCarroceria tipoCarroceria,
                     Double capacidadCargaKg, Boolean traccion4x4) throws DatoInvalidoException {
        // Delega a la superclase Vehiculo la carga y validación de los 7 datos comunes
        super(marca, modelo, anio, patente, color, kilometraje, precioBase);
        // Inicializa los atributos específicos de Camioneta pasando por sus setters con validación
        setTipoCarroceria(tipoCarroceria);
        setCapacidadCargaKg(capacidadCargaKg);
        setTraccion4x4(traccion4x4);
    }

    // Constructor sobrecargado cuando el operador escribe una carrocería nueva manualmente
    public Camioneta(String marca, String modelo, Integer anio, String patente, String color,
                     Integer kilometraje, Double precioBase, String carroceriaPersonalizada,
                     Double capacidadCargaKg, Boolean traccion4x4) throws DatoInvalidoException {
        super(marca, modelo, anio, patente, color, kilometraje, precioBase);
        setCarroceriaPersonalizada(carroceriaPersonalizada);
        setCapacidadCargaKg(capacidadCargaKg);
        setTraccion4x4(traccion4x4);
    }

    // =========================================================================
    // 3. MÉTODOS POLIMÓRFICOS SOBRESCRITOS (@Override)
    // =========================================================================

    // Implementa el método abstracto de Vehiculo identificando esta subclase como CAMIONETA
    @Override
    public TipoVehiculo getTipo() {
        return TipoVehiculo.CAMIONETA;
    }

    // Devuelve la carrocería personalizada si fue cargada; de lo contrario, el nombre del Enum
    @Override
    public String getCarroceria() {
        if (carroceriaPersonalizada != null && !carroceriaPersonalizada.trim().isEmpty()) {
            return carroceriaPersonalizada.trim();
        }
        return tipoCarroceria != null ? tipoCarroceria.toString() : "Sin especificar";
    }

    // Implementa el contrato de Vendible: aplica un 15% si es 0 KM (1.15) o un 8% si es usada (1.08),
    // y suma un adicional del 5% (+ 0.05) si la camioneta cuenta con tracción 4x4.
    @Override
    public double calcularPrecioFinal() {
        double factor = esNuevo() ? 1.15 : 1.08;
        // Boolean.TRUE.equals(...) evita NullPointerException si traccion4x4 llegara a ser null
        if (Boolean.TRUE.equals(traccion4x4)) {
            factor += 0.05;
        }
        return getPrecioBase() * factor;
    }

    // =========================================================================
    // 4. MÉTODOS ACCESORES Y MUTADORES (GETTERS Y SETTERS CON VALIDACIÓN)
    // =========================================================================

    public TipoCarroceria getTipoCarroceria() {
        return tipoCarroceria;
    }

    public void setTipoCarroceria(TipoCarroceria tipoCarroceria) throws DatoInvalidoException {
        if (tipoCarroceria == null) {
            throw new DatoInvalidoException("Debe seleccionar un tipo de carrocería.");
        }
        this.tipoCarroceria = tipoCarroceria;
        this.carroceriaPersonalizada = null; // Anula la carrocería personalizada para evitar ambigüedad
    }

    public String getCarroceriaPersonalizada() {
        return carroceriaPersonalizada;
    }

    public void setCarroceriaPersonalizada(String carroceriaPersonalizada) throws DatoInvalidoException {
        if (carroceriaPersonalizada == null || carroceriaPersonalizada.trim().isEmpty()) {
            throw new DatoInvalidoException("La descripción de la carrocería no puede estar vacía.");
        }
        this.carroceriaPersonalizada = carroceriaPersonalizada.trim();
        this.tipoCarroceria = null; // Anula el Enum para priorizar el texto ingresado manualmente
    }

    public Double getCapacidadCargaKg() {
        return capacidadCargaKg;
    }

    // Regla de negocio: ninguna camioneta puede registrarse con capacidad de carga nula, cero o negativa
    public void setCapacidadCargaKg(Double capacidadCargaKg) throws DatoInvalidoException {
        if (capacidadCargaKg == null || capacidadCargaKg <= 0) {
            throw new DatoInvalidoException("La capacidad de carga debe ser mayor a 0 kg.");
        }
        this.capacidadCargaKg = capacidadCargaKg;
    }

    public Boolean getTraccion4x4() {
        return traccion4x4;
    }

    // Si pasan null, asume por defecto false (tracción simple 4x2)
    public void setTraccion4x4(Boolean traccion4x4) {
        this.traccion4x4 = traccion4x4 != null ? traccion4x4 : false;
    }

    // Redefine toString() reutilizando la salida base de Vehiculo ('super.toString()')
    // y agregando la capacidad de carga, si es 4x4 ("Sí"/"No") y el precio final calculado.
    @Override
    public String toString() {
        long precioFinal = Math.round(calcularPrecioFinal());
        String texto4x4 = Boolean.TRUE.equals(traccion4x4) ? "Sí" : "No";
        return super.toString() + " | Carga: " + Math.round(capacidadCargaKg) + " kg | 4x4: " + texto4x4 + " | Precio Final: $" + precioFinal;
    }
}