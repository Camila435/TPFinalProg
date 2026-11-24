package concesionaria.dominio;

/**
 * Excepción lanzada cuando la cantidad de puertas de un automóvil
 * se encuentra fuera del rango permitido (1 a 5 puertas).
 *
 * @author GyQ Concesionaria
 * @version 1.0
 */
public class PuertasInsuficientesException extends DatoInvalidoException {

    private static final long serialVersionUID = 1L;

    /**
     * Crea una nueva excepción por cantidad de puertas inválida.
     *
     * @param mensaje Descripción del error de validación.
     */
    public PuertasInsuficientesException(String mensaje) {
        super(mensaje);
    }
}