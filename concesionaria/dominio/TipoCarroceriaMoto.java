package concesionaria.dominio;

public enum TipoCarroceriaMoto {
    ENDURO("Enduro"),
    SCOOTER("Scooter"),
    STREET("Street"),
    CHOPPER("Chopper"),
    NAKED("Naked");

    private final String descripcion;

    TipoCarroceriaMoto(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public String toString() {
        return descripcion;
    }
}