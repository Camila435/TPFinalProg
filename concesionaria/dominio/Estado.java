package concesionaria.dominio;

/**
 * Enumeración que representa la condición comercial de uso de un vehículo.
 *
 * @author Camila Quintero
 * @author Guadalupe Gallardo
 * @version 1.0
 */
public enum Estado {
    /** Unidad sin rodar (0 kilómetros). */
    NUEVO("0 KM"),
    /** Unidad con kilometraje previo registrado. */
    USADO("Usado");

    private final String descripcion;

    /**
     * Construye una constante de estado con su etiqueta legible.
     *
     * @param descripcion Texto descriptivo del estado.
     */
    Estado(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public String toString() {
        return descripcion;
    }
}