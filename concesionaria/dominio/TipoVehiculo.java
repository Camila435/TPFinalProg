package concesionaria.dominio;

/**
 * Categorías principales de vehículos comercializados en la concesionaria.
 *
 * @author GyQ Concesionaria
 * @version 1.0
 */
public enum TipoVehiculo {
    /** Vehículo de pasajeros estándar de 3 a 5 puertas. */
    AUTOMOVIL("Automóvil"),
    /** Vehículo utilitario, todoterreno o de carga ligera. */
    CAMIONETA("Camioneta"),
    /** Vehículo motorizado de dos ruedas. */
    MOTOCICLETA("Motocicleta");

    private final String descripcion;

    /**
     * Construye un tipo de vehículo con su descripción.
     *
     * @param descripcion Nombre legible de la categoría.
     */
    TipoVehiculo(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public String toString() {
        return descripcion;
    }
}