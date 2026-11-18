package concesionaria.dominio;

public class PuertasInsuficientesException extends DatoInvalidoException {
    public PuertasInsuficientesException(String mensaje) {
        super(mensaje);
    }
}