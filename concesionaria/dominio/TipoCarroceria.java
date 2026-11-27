package concesionaria.dominio;

/**
 * Tipos de carrocería disponibles para automóviles y camionetas.
 *
 * @author Camila Quintero
 * @author Guadalupe Gallardo
 * @version 1.0
 */
// Enumera las carrocerías que el sistema permite asignar a autos y camionetas.
public enum TipoCarroceria {
    // Cada constante se vincula con el nombre que se mostrará al usuario.
    SEDAN("Sedán"),
    HATCHBACK("Hatchback"),
    COUPE("Coupé"),
    FAMILIAR("Familiar"),
    SUV("SUV"),
    PICKUP("Pick-up"),
    FURGON("Furgón");

    // Guarda la etiqueta legible asociada a cada tipo de carrocería.
    private final String descripcion;

    /**
     * Construye un tipo de carrocería con su descripción.
     *
     * @param descripcion Nombre de la carrocería.
     */
    // Asigna a la constante la descripción recibida.
    TipoCarroceria(String descripcion) {
        this.descripcion = descripcion;
    }

    /**
     * Devuelve las carrocerías válidas exclusivamente para automóviles.
     *
     * @return Arreglo de carrocerías aplicables a {@link Auto}.
     */
    // Devuelve solo las opciones que corresponden a los automóviles.
    public static TipoCarroceria[] paraAutos() {
        return new TipoCarroceria[] { SEDAN, HATCHBACK, COUPE, FAMILIAR };
    }

    /**
     * Devuelve las carrocerías válidas exclusivamente para camionetas.
     *
     * @return Arreglo de carrocerías aplicables a {@link Camioneta}.
     */
    // Devuelve solo las opciones que corresponden a las camionetas.
    public static TipoCarroceria[] paraCamionetas() {
        return new TipoCarroceria[] { SUV, PICKUP, FURGON };
    }

    // Hace que al mostrar la constante se utilice su descripción en lugar de su identificador.
    @Override
    public String toString() {
        // Retorna el texto descriptivo configurado para esta carrocería.
        return descripcion;
    }
}