package concesionaria.dominio;

/**
 * Excepción verificada base para errores de validación de reglas de negocio
 * en las entidades del dominio de la concesionaria.
 *
 * @author Camila Quintero
 * @author Guadalupe Gallardo
 * @version 1.0
 */
// Esta clase representa un error producido al validar datos del dominio.
// Al extender Exception, obliga a quien la usa a manejar o declarar el error.
public class DatoInvalidoException extends Exception {

    // Identifica la versión de la clase al serializarla y deserializarla.
    private static final long serialVersionUID = 1L;

    /**
     * Crea una nueva excepción con el detalle del error de validación.
     *
     * @param mensaje Descripción del dato inválido.
     */
    // Recibe el texto descriptivo del problema y lo guarda en la excepción base.
    public DatoInvalidoException(String mensaje) {
        // Exception almacenará el mensaje para poder recuperarlo con getMessage().
        super(mensaje);
    }
}