package concesionaria.dominio;

/**
 * Estilos y configuraciones de diseño para motocicletas.
 *
 * @author Camila Quintero
 * @author Guadalupe Gallardo
 * @version 1.0
 */
public enum TipoCarroceriaMoto {
    ENDURO("Enduro"),
    SCOOTER("Scooter"),
    STREET("Street"),
    CHOPPER("Chopper"),
    NAKED("Naked");

    private final String descripcion;

    /**
     * Construye un estilo de motocicleta con su descripción.
     *
     * @param descripcion Nombre legible del estilo.
     */
    TipoCarroceriaMoto(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public String toString() {
        return descripcion;
    }
}