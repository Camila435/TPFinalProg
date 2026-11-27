package concesionaria.dominio;

/**
 * Categorías principales de vehículos comercializados en la concesionaria.
 *
 * @author Camila Quintero
 * @author Guadalupe Gallardo
 * @version 1.0
 */
// Enumera las categorías de vehículos que maneja el sistema.
public enum TipoVehiculo {
    /** Vehículo de pasajeros estándar de 3 a 5 puertas. */
    // Representa un automóvil y su nombre legible para mostrarlo en pantalla.
    AUTOMOVIL("Automóvil"),
    /** Vehículo utilitario, todoterreno o de carga ligera. */
    // Representa una camioneta y su nombre legible.
    CAMIONETA("Camioneta"),
    /** Vehículo motorizado de dos ruedas. */
    // Representa una motocicleta y su nombre legible.
    MOTOCICLETA("Motocicleta");

    // Conserva la etiqueta asociada a cada categoría.
    private final String descripcion;

    /**
     * Construye un tipo de vehículo con su descripción.
     *
     * @param descripcion Nombre legible de la categoría.
     */
    // Asigna la descripción al tipo de vehículo que se está inicializando.
    TipoVehiculo(String descripcion) {
        this.descripcion = descripcion;
    }

    // Permite que la categoría se muestre usando su descripción.
    @Override
    public String toString() {
        // Retorna la etiqueta legible de esta constante.
        return descripcion;
    }
}