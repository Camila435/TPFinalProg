package concesionaria.dominio;

/**
 * TFI Programacion II - Concesionaria Multimarcas.
 * Clase concreta: herencia de Vehiculo, polimorfismo mediante @Override,
 * encapsulamiento y reutilizacion de validaciones.
 * El estado NUEVO/USADO se determina en Vehiculo segun kilometraje.
 */
public class Motocicleta extends Vehiculo {
    /** Atributo especifico de esta clase; el resto se hereda de Vehiculo. */
    private TipoCarroceriaMoto carroceria;

    /** Constructor sin argumentos para herramientas de tipo JavaBean. */
    public Motocicleta() { super(); }

    /**
     * Construye un motocicleta y valida los datos comunes en Vehiculo.
     * @param marca marca comercial
     * @param modelo modelo comercial
     * @param anio anio de fabricacion
     * @param patente identificacion del vehiculo
     * @param color color declarado
     * @param kilometraje 0 para nuevo; mayor a 0 para usado
     * @param precioBase precio positivo
     * @param carroceria categoria de carroceria o tipo
     * @throws DatoInvalidoException si algun dato no es valido
     */
    public Motocicleta(String marca, String modelo, Integer anio, String patente,
            String color, Integer kilometraje, Double precioBase,
            TipoCarroceriaMoto carroceria) throws DatoInvalidoException {
        super(marca, modelo, anio, patente, color, kilometraje, precioBase);
        setCarroceria(carroceria);
    }

    /** @return carroceria configurada (puede ser null antes de cargar datos). */
    public TipoCarroceriaMoto getTipoCarroceria() { return carroceria; }

    /**
     * Valida que la categoria corresponda a este tipo de vehiculo.
     * @throws DatoInvalidoException cuando el valor es nulo o incompatible
     */
    public void setCarroceria(TipoCarroceriaMoto carroceria) throws DatoInvalidoException {
        if (carroceria == null) {
            throw new DatoInvalidoException("La carroceria/tipo es obligatorio.");
        }
        this.carroceria = carroceria;
    }

    /** Polimorfismo: cada subclase informa su propio tipo. */
    @Override
    public TipoVehiculo getTipo() { return TipoVehiculo.MOTOCICLETA; }

    /** Polimorfismo: descripcion de la carroceria o tipo especifico. */
    @Override
    public String getCarroceria() {
        return carroceria == null ? "Sin definir" : carroceria.toString();
    }

    /**
     * Implementacion de la interfaz Vendible heredada de Vehiculo.
     * Regla inicial: precio final = precio base; no se inventan recargos.
     */
    @Override
    public double calcularPrecioFinal() { return getPrecioBase(); }
}
