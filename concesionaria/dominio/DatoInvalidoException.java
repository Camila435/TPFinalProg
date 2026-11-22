package concesionaria.dominio;

/**
 * Excepción verificada base para errores de validación de reglas de negocio
 * en las entidades del dominio de la concesionaria.
 *
 * @author GyQ Concesionaria
 * @version 1.0
 */
public class DatoInvalidoException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * Crea una nueva excepción con el detalle del error de validación.
     *
     * @param mensaje Descripción del dato inválido.
     */
    public DatoInvalidoException(String mensaje) {
        super(mensaje);
    }
}