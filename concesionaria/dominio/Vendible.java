package concesionaria.dominio;

/**
 * Contrato comercial que define el cálculo del precio final de venta
 * para cualquier unidad comercializable en GyQ Concesionaria.
 *
 * @author Camila Quintero
 * @author Guadalupe Gallardo
 * @version 1.0
 */
// Define un contrato común para las clases de objetos que pueden venderse.
public interface Vendible {

    /**
     * Calcula el precio final de venta aplicando impuestos, aranceles y recargos
     * correspondientes según el tipo y estado del vehículo.
     *
     * @return Precio final en moneda local/divisa configurada.
     */
    // Las clases que implementen esta interfaz deben calcular y devolver el precio final.
    double calcularPrecioFinal();
}