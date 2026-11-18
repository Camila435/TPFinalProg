package concesionaria.dominio;

import java.time.Year;
import java.util.Objects;

/**
 * Superclase abstracta que representa la base de todo vehículo en GyQ Concesionaria.
 * Aplica encapsulamiento estricto, clases Wrapper, validación de dominio y contrato equals/hashCode.
 */
public abstract class Vehiculo implements Vendible {

    public static final int ANIO_MINIMO = 1950;

    private String marca;
    private String modelo;
    private Integer anio;
    private String patente;
    private String color;
    private Integer kilometraje;
    private Double precioBase;

    /** Constructor vacío requerido por el patrón JavaBean. */
    public Vehiculo() {
    }

    /** Constructor parametrizado con validaciones de negocio. */
    public Vehiculo(String marca, String modelo, Integer anio, String patente,
                    String color, Integer kilometraje, Double precioBase) throws DatoInvalidoException {
        setMarca(marca);
        setModelo(modelo);
        setAnio(anio);
        setPatente(patente);
        setColor(color);
        setKilometraje(kilometraje);
        setPrecioBase(precioBase);
    }

    // --- MÉTODOS DE NEGOCIO Y COMPORTAMIENTO ---

    public boolean esNuevo() {
        return this.kilometraje != null && this.kilometraje.intValue() == 0;
    }

    public Estado getEstado() {
        return esNuevo() ? Estado.NUEVO : Estado.USADO;
    }

    public static int anioMaximo() {
        return Year.now().getValue() + 1;
    }

    public static String normalizarPatente(String patente) {
        return patente == null ? "" : patente.trim().toUpperCase().replace(" ", "").replace("-", "");
    }

    public static boolean esPatenteValida(String patente) {
        String p = normalizarPatente(patente);
        // Formato viejo (ABC123) o Mercosur (AB123CD / A123BCD)
        return p.matches("^[A-Z]{3}\\d{3}$") || p.matches("^[A-Z]{2}\\d{3}[A-Z]{2}$") || p.matches("^[A-Z]\\d{3}[A-Z]{3}$");
    }

    // Métodos abstractos que cada subclase debe resolver a su manera (Polimorfismo)
    public abstract TipoVehiculo getTipo();
    public abstract String getCarroceria();

    // --- GETTERS Y SETTERS CON VALIDACIÓN DEFENSIVA ---

    public String getMarca() { return marca; }
    public void setMarca(String marca) throws DatoInvalidoException {
        if (marca == null || marca.trim().isEmpty()) {
            throw new DatoInvalidoException("La marca no puede estar vacía.");
        }
        this.marca = marca.trim();
    }

    public String getModelo() { return modelo; }
    public void setModelo(String modelo) throws DatoInvalidoException {
        if (modelo == null || modelo.trim().isEmpty()) {
            throw new DatoInvalidoException("El modelo no puede estar vacío.");
        }
        this.modelo = modelo.trim();
    }

    public Integer getAnio() { return anio; }
    public void setAnio(Integer anio) throws DatoInvalidoException {
        if (anio == null || anio.intValue() < ANIO_MINIMO || anio.intValue() > anioMaximo()) {
            throw new DatoInvalidoException("El año debe estar entre " + ANIO_MINIMO + " y " + anioMaximo() + ".");
        }
        this.anio = anio;
    }

    public String getPatente() { return patente; }
    public void setPatente(String patente) throws PatenteInvalidaException {
        String normalizada = normalizarPatente(patente);
        if (!esPatenteValida(normalizada)) {
            throw new PatenteInvalidaException("La patente '" + patente + "' no tiene un formato válido (ej: ABC123 o AB123CD).");
        }
        this.patente = normalizada;
    }

    public String getColor() { return color; }
    public void setColor(String color) throws DatoInvalidoException {
        if (color == null || color.trim().isEmpty()) {
            throw new DatoInvalidoException("El color no puede estar vacío.");
        }
        this.color = color.trim();
    }

    public Integer getKilometraje() { return kilometraje; }
    public void setKilometraje(Integer kilometraje) throws DatoInvalidoException {
        if (kilometraje == null || kilometraje.intValue() < 0) {
            throw new DatoInvalidoException("El kilometraje no puede ser negativo.");
        }
        this.kilometraje = kilometraje;
    }

    public Double getPrecioBase() { return precioBase; }
    public void setPrecioBase(Double precioBase) throws DatoInvalidoException {
        if (precioBase == null || precioBase.doubleValue() <= 0) {
            throw new DatoInvalidoException("El precio base debe ser mayor a $0.");
        }
        this.precioBase = precioBase;
    }

    // --- CONTRATO DE OBJECT (Identidad por Patente) ---

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vehiculo vehiculo = (Vehiculo) o;
        return Objects.equals(this.patente, vehiculo.patente);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.patente);
    }

    @Override
    public String toString() {
        return String.format("[%s] %s %s (%d) | Color: %s | Carrocería: %s | %s (%d km) | Precio Base: $%.2f",
                patente, marca, modelo, anio, color, getCarroceria(), getEstado(), kilometraje, precioBase);
    }
}