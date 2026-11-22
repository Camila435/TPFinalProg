package concesionaria.dominio;

/**
 * Tipos de carrocería disponibles para automóviles y camionetas.
 *
 * @author GyQ Concesionaria
 * @version 1.0
 */
public enum TipoCarroceria {
    SEDAN("Sedán"),
    HATCHBACK("Hatchback"),
    COUPE("Coupé"),
    FAMILIAR("Familiar"),
    SUV("SUV"),
    PICKUP("Pick-up"),
    FURGON("Furgón");

    private final String descripcion;

    /**
     * Construye un tipo de carrocería con su descripción.
     *
     * @param descripcion Nombre de la carrocería.
     */
    TipoCarroceria(String descripcion) {
        this.descripcion = descripcion;
    }

    /**
     * Devuelve las carrocerías válidas exclusivamente para automóviles.
     *
     * @return Arreglo de carrocerías aplicables a {@link Auto}.
     */
    public static TipoCarroceria[] paraAutos() {
        return new TipoCarroceria[] { SEDAN, HATCHBACK, COUPE, FAMILIAR };
    }

    /**
     * Devuelve las carrocerías válidas exclusivamente para camionetas.
     *
     * @return Arreglo de carrocerías aplicables a {@link Camioneta}.
     */
    public static TipoCarroceria[] paraCamionetas() {
        return new TipoCarroceria[] { SUV, PICKUP, FURGON };
    }

    @Override
    public String toString() {
        return descripcion;
    }
}