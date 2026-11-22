package concesionaria.dominio;

/**
 * Contrato comercial que define el cálculo del precio final de venta
 * para cualquier unidad comercializable en GyQ Concesionaria.
 *
 * @author GyQ Concesionaria
 * @version 1.0
 */
public interface Vendible {

    /**
     * Calcula el precio final de venta aplicando impuestos, aranceles y recargos
     * correspondientes según el tipo y estado del vehículo.
     *
     * @return Precio final en moneda local/divisa configurada.
     */
    double calcularPrecioFinal();
}