package concesionaria.dominio;

public class PatenteInvalidaException extends DatoInvalidoException {
    public PatenteInvalidaException(String mensaje) {
        super(mensaje);
    }
}