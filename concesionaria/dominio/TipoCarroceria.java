package concesionaria.dominio;

public enum TipoCarroceria {
    SEDAN("Sedán"),
    HATCHBACK("Hatchback"),
    COUPE("Coupé"),
    FAMILIAR("Familiar"),
    SUV("SUV"),
    PICKUP("Pick-up"),
    FURGON("Furgón");

    private final String descripcion;

    TipoCarroceria(String descripcion) {
        this.descripcion = descripcion;
    }

    public static TipoCarroceria[] paraAutos() {
        return new TipoCarroceria[] { SEDAN, HATCHBACK, COUPE, FAMILIAR };
    }

    public static TipoCarroceria[] paraCamionetas() {
        return new TipoCarroceria[] { SUV, PICKUP, FURGON };
    }

    @Override
    public String toString() {
        return descripcion;
    }
}