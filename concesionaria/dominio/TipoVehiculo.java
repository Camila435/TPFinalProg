package concesionaria.dominio;

public enum TipoVehiculo {
    AUTOMOVIL("Automóvil"),
    CAMIONETA("Camioneta"),
    MOTOCICLETA("Motocicleta");

    private final String descripcion;

    TipoVehiculo(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public String toString() {
        return descripcion;
    }
}