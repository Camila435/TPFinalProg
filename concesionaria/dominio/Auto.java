package concesionaria.dominio;

/**
 * Representa un automóvil dentro del inventario de ventas de GyQ Concesionaria.
 * Cumple con el estándar JavaBean heredando {@link java.io.Serializable} desde {@link Vehiculo}.
 *
 * @author GyQ Concesionaria
 * @version 1.2
 */
// 'extends Vehiculo' aplica HERENCIA: Auto hereda todos los atributos y métodos de la superclase abstracta Vehiculo,
// además de heredar indirectamente las interfaces Vendible y Serializable.
public class Auto extends Vehiculo {

    // Identificador de versión para la serialización binaria (ObjectOutputStream / ObjectInputStream).
    // Evita InvalidClassException si se modifica levemente la clase en el futuro.
    private static final long serialVersionUID = 1L;

    // =========================================================================
    // 1. AMBIENTE DE ATRIBUTOS PROPIOS (ENCAPSULAMIENTO ESTRICTO)
    // =========================================================================
    // Son 'private' para proteger el estado interno del objeto: nadie desde afuera puede modificarlos
    // directamente sin pasar por las validaciones de los métodos setters.
    private Integer cantPuertas;              // Usa clase Wrapper Integer en lugar de int (cumple estándar JavaBean)
    private TipoCarroceria tipoCarroceria;  // Enum con las carrocerías predefinidas (SEDAN, HATCHBACK, etc.)
    private String carroceriaPersonalizada; // Guarda el texto si el operador ingresa una carrocería nueva ("Otra")

    // =========================================================================
    // 2. CONSTRUCTORES (SOBRECARGA DE CONSTRUCTORES)
    // =========================================================================

    /**
     * Constructor vacío requerido por el estándar JavaBean.
     */
    public Auto() {
        super(); // Llama al constructor sin parámetros de la superclase Vehiculo
    }

    /**
     * Construye una instancia completa de automóvil con una carrocería del catálogo (Enum).
     */
    public Auto(String marca, String modelo, Integer anio, String patente,
                String color, Integer kilometraje, Double precioBase,
                Integer cantPuertas, TipoCarroceria tipoCarroceria) throws DatoInvalidoException {
        // 'super(...)' delega a Vehiculo la inicialización y validación de los 7 atributos comunes
        super(marca, modelo, anio, patente, color, kilometraje, precioBase);
        // Luego valida e inicializa los 2 atributos propios de Auto usando sus setters (no asignación directa)
        setCantPuertas(cantPuertas);
        setTipoCarroceria(tipoCarroceria);
    }

    /**
     * Constructor sobrecargado: permite instanciar un automóvil cuando el operador
     * ingresa manualmente un tipo de carrocería nuevo que no está en el Enum.
     */
    public Auto(String marca, String modelo, Integer anio, String patente,
                String color, Integer kilometraje, Double precioBase,
                Integer cantPuertas, String carroceriaPersonalizada) throws DatoInvalidoException {
        super(marca, modelo, anio, patente, color, kilometraje, precioBase);
        setCantPuertas(cantPuertas);
        setCarroceriaPersonalizada(carroceriaPersonalizada);
    }

    // =========================================================================
    // 3. MÉTODOS ACCESORES Y MUTADORES (GETTERS Y SETTERS CON VALIDACIÓN)
    // =========================================================================

    public Integer getCantPuertas() {
        return cantPuertas;
    }

   // Lanza una excepción propia (PuertasInsuficientesException, que hereda de DatoInvalidoException)
    // si intentan crear o modificar un auto con menos de 1 o más de 5 puertas.
    public void setCantPuertas(Integer cantPuertas) throws PuertasInsuficientesException {
        if (cantPuertas == null || cantPuertas < 1 || cantPuertas > 5) {
            throw new PuertasInsuficientesException("Un automóvil debe tener entre 1 y 5 puertas.");
        }
        this.cantPuertas = cantPuertas;
    }

    public TipoCarroceria getTipoCarroceria() {
        return tipoCarroceria;
    }

    // Regla de dominio: impide asignarle a un Auto carrocerías exclusivas de Camioneta (SUV, PICKUP, FURGON)
    public void setTipoCarroceria(TipoCarroceria tipoCarroceria) throws DatoInvalidoException {
        if (tipoCarroceria == null || tipoCarroceria == TipoCarroceria.SUV
                || tipoCarroceria == TipoCarroceria.PICKUP || tipoCarroceria == TipoCarroceria.FURGON) {
            throw new DatoInvalidoException("La carrocería '" + tipoCarroceria + "' no corresponde a un automóvil.");
        }
        this.tipoCarroceria = tipoCarroceria;
        this.carroceriaPersonalizada = null; // Limpia el texto personalizado si se eligió una opción del Enum
    }

    public String getCarroceriaPersonalizada() {
        return carroceriaPersonalizada;
    }

    public void setCarroceriaPersonalizada(String carroceriaPersonalizada) throws DatoInvalidoException {
        if (carroceriaPersonalizada == null || carroceriaPersonalizada.trim().isEmpty()) {
            throw new DatoInvalidoException("La descripción de la carrocería no puede estar vacía.");
        }
        this.carroceriaPersonalizada = carroceriaPersonalizada.trim();
        this.tipoCarroceria = null; // Limpia el Enum si se cargó una carrocería personalizada
    }

    // =========================================================================
    // 4. MÉTODOS POLIMÓRFICOS SOBRESCRITOS (@Override)
    // =========================================================================

    // Implementa el método abstracto de Vehiculo indicando la categoría fija de esta subclase
    @Override
    public TipoVehiculo getTipo() {
        return TipoVehiculo.AUTOMOVIL;
    }

    // Implementa el método abstracto de Vehiculo devolviendo la carrocería personalizada o la del Enum
    @Override
    public String getCarroceria() {
        if (carroceriaPersonalizada != null && !carroceriaPersonalizada.trim().isEmpty()) {
            return carroceriaPersonalizada.trim();
        }
        return tipoCarroceria != null ? tipoCarroceria.toString() : "Sin definir";
    }

    // Implementa el contrato de la interfaz Vendible (heredado desde Vehiculo):
    // Aplica un 10% de recargo comercial si es 0 KM (esNuevo() == true), o un 4% si es usado.
    @Override
    public double calcularPrecioFinal() {
        double recargo = esNuevo() ? 1.10 : 1.04;
        return getPrecioBase() * recargo;
    }

    // Redefine toString() reutilizando el formato base de la superclase ('super.toString()')
    // y concatenándole los datos propios del automóvil (puertas y precio final calculado).
    @Override
    public String toString() {
        return super.toString() + String.format(" | Puertas: %d | Precio Final: $%.2f",
                cantPuertas, calcularPrecioFinal());
    }
}