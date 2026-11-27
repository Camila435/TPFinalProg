package concesionaria.dominio;

/**
 * Enumeración que representa la condición comercial de uso de un vehículo.
 *
 * @author Camila Quintero
 * @author Guadalupe Gallardo
 * @version 1.0
 */
// Enum: define un conjunto cerrado de estados posibles para un vehículo.
public enum Estado {
    /** Unidad sin rodar (0 kilómetros). */
    // Representa un vehículo nuevo y guarda como texto legible "0 KM".
    NUEVO("0 KM"),
    /** Unidad con kilometraje previo registrado. */
    // Representa un vehículo usado y guarda como descripción "Usado".
    USADO("Usado");

    // Almacena el texto asociado a cada estado y no puede modificarse después.
    private final String descripcion;

    /**
     * Construye una constante de estado con su etiqueta legible.
     *
     * @param descripcion Texto descriptivo del estado.
     */
    // El constructor asigna a cada constante la descripción recibida.
    Estado(String descripcion) {
        this.descripcion = descripcion;
    }

    // Permite mostrar o comparar visualmente el estado mediante su texto.
    @Override
    public String toString() {
        // Devuelve la descripción guardada por la constante del enum.
        return descripcion;
    }
}