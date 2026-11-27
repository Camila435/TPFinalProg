package concesionaria.dominio;

/**
 * Estilos y configuraciones de diseño para motocicletas.
 *
 * @author Camila Quintero
 * @author Guadalupe Gallardo
 * @version 1.0
 */
// Enumera los estilos de carrocería disponibles para las motocicletas.
public enum TipoCarroceriaMoto {
    // Cada opción guarda el nombre legible que se mostrará en la aplicación.
    ENDURO("Enduro"),
    SCOOTER("Scooter"),
    STREET("Street"),
    CHOPPER("Chopper"),
    NAKED("Naked");

    // Almacena el nombre asociado a cada estilo.
    private final String descripcion;

    /**
     * Construye un estilo de motocicleta con su descripción.
     *
     * @param descripcion Nombre legible del estilo.
     */
    // Inicializa cada constante con su descripción.
    TipoCarroceriaMoto(String descripcion) {
        this.descripcion = descripcion;
    }

    // Personaliza la representación textual de cada constante del enum.
    @Override
    public String toString() {
        // Devuelve el nombre legible guardado en la constante.
        return descripcion;
    }
}