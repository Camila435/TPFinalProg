package concesionaria.dominio;

/**
 * Excepción lanzada cuando la cantidad de puertas de un automóvil
 * se encuentra fuera del rango permitido (1 a 5 puertas).
 *
 * @author Camila Quintero
 * @author Guadalupe Gallardo
 * @version 1.0
 */
// Esta excepción especial representa errores relacionados con la cantidad de puertas.
// Hereda de DatoInvalidoException para agrupar todas las validaciones dentro de una excepción base.
public class PuertasInsuficientesException extends DatoInvalidoException {

    // Identifica la versión de esta clase si el objeto se serializa.
    private static final long serialVersionUID = 1L;

    /**
     * Crea una nueva excepción por cantidad de puertas inválida.
     *
     * @param mensaje Descripción del error de validación.
     */
    // Recibe una descripción del error y la conserva para informar al usuario.
    public PuertasInsuficientesException(String mensaje) {
        // Envía el mensaje al constructor de DatoInvalidoException.
        super(mensaje);
    }
}