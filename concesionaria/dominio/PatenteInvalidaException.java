package concesionaria.dominio;

/**
 * Excepción lanzada cuando una patente no cumple con el formato nacional o Mercosur,
 * o cuando ya existe duplicada en el inventario.
 *
 * @author Camila Quintero
 * @author Guadalupe Gallardo
 * @version 1.0
 */
// Representa errores específicos relacionados con la patente de un vehículo.
// Hereda de DatoInvalidoException para tratarla como una excepción de datos inválidos.
public class PatenteInvalidaException extends DatoInvalidoException {

    // Define el identificador usado al serializar y deserializar la excepción.
    private static final long serialVersionUID = 1L;

    /**
     * Crea una nueva excepción de patente inválida.
     *
     * @param mensaje Detalle del error de formato o duplicidad.
     */
    // Guarda el detalle del problema mediante la jerarquía de excepciones.
    public PatenteInvalidaException(String mensaje) {
        // Reenvía el mensaje al constructor heredado de DatoInvalidoException.
        super(mensaje);
    }
}