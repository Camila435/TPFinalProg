package concesionaria.dominio;

/**
 * Excepción lanzada cuando una patente no cumple con el formato nacional o Mercosur,
 * o cuando ya existe duplicada en el inventario.
 *
 * @author Camila Quintero
 * @author Guadalupe Gallardo
 * @version 1.0
 */
public class PatenteInvalidaException extends DatoInvalidoException {

    private static final long serialVersionUID = 1L;

    /**
     * Crea una nueva excepción de patente inválida.
     *
     * @param mensaje Detalle del error de formato o duplicidad.
     */
    public PatenteInvalidaException(String mensaje) {
        super(mensaje);
    }
}