package concesionaria.dominio;

public enum Estado {
    NUEVO("0 KM"),
    USADO("Usado");

    private final String descripcion;

    Estado(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public String toString() {
        return descripcion;
    }
}