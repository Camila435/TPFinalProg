package concesionaria.dominio;

import java.io.Serializable;
import java.time.Year;
import java.util.Objects;

/**
 * Superclase abstracta que representa la base de todo vehículo en GyQ Concesionaria.
 * <p>
 * Implementa el estándar <b>JavaBean</b> (constructor sin parámetros, atributos privados
 * encapsulados, getters/setters públicos e implementación de {@link Serializable} con
 * {@code serialVersionUID}) para permitir su persistencia en flujos binarios.
 * </p>
 *
 * @author Camila Quintero
 * @author Guadalupe Gallardo
 * @version 1.2
 */
// 'abstract class' impide crear un Vehiculo genérico con 'new Vehiculo()': obliga a instanciar
// una subclase concreta (Auto, Camioneta o Motocicleta).
// 'implements Vendible, Serializable' firma el contrato comercial y habilita la persistencia en disco.
public abstract class Vehiculo implements Vendible, Serializable {

    // Identificador de versión para la serialización binaria (.dat), exigido por Serializable
    private static final long serialVersionUID = 1L;

    // =========================================================================
    // 1. CONSTANTES Y AMBIENTE DE ATRIBUTOS COMUNES (ENCAPSULAMIENTO ESTRICTO)
    // =========================================================================

    /** Año mínimo de fabricación admitido en el sistema. */
    // 'public static final' define una constante compartida por toda la jerarquía sin gastar memoria por objeto
    public static final int ANIO_MINIMO = 1950;

    // Atributos 'private' con clases Wrapper (Integer, Double) según el estándar JavaBean:
    // solo se acceden o modifican mediante getters y setters validados.
    private String marca;
    private String modelo;
    private Integer anio;
    private String patente;
    private String color;
    private Integer kilometraje;
    private Double precioBase;

    // =========================================================================
    // 2. CONSTRUCTORES (SOBRECARGA DE CONSTRUCTORES)
    // =========================================================================

    /**
     * Constructor público sin parámetros requerido por el estándar JavaBean
     * y los mecanismos de deserialización.
     */
    public Vehiculo() {
    }

    /**
     * Constructor parametrizado que inicializa y valida todos los atributos comunes.
     *
     * @param marca       Fabricante del vehículo.
     * @param modelo      Denominación comercial del modelo.
     * @param anio        Año de fabricación.
     * @param patente     Dominio o matrícula identificatoria.
     * @param color       Color exterior de la unidad.
     * @param kilometraje Kilómetros recorridos (0 para unidades nuevas).
     * @param precioBase  Precio base de lista antes de impuestos.
     * @throws DatoInvalidoException Si alguno de los valores incumple las reglas de dominio.
     */
    // En lugar de asignar 'this.marca = marca' directo, delega cada dato a su setter
    // para garantizar que ningún vehículo nazca en un estado inválido.
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

    // =========================================================================
    // 3. MÉTODOS DE COMPORTAMIENTO Y UTILITARIOS DEL DOMINIO
    // =========================================================================

    /**
     * Indica si el vehículo es 0 KM.
     *
     * @return {@code true} si el kilometraje es exactamente 0; {@code false} en caso contrario.
     */
    // Verifica que kilometraje no sea null antes de extraer el primitivo con .intValue()
    public boolean esNuevo() {
        return this.kilometraje != null && this.kilometraje.intValue() == 0;
    }

    /**
     * Obtiene el estado comercial ({@link Estado#NUEVO} o {@link Estado#USADO}).
     *
     * @return Estado correspondiente según el kilometraje.
     */
    // Atributo derivado: calcula el Enum Estado dinámicamente en base al kilometraje (0 km = NUEVO)
    public Estado getEstado() {
        return esNuevo() ? Estado.NUEVO : Estado.USADO;
    }

    /**
     * Calcula el año máximo permitido (año calendario actual + 1 para modelos adelantados).
     *
     * @return Año máximo válido.
     */
    // Usa la API java.time.Year para obtener el año actual del sistema operativo dinámicamente
    public static int anioMaximo() {
        return Year.now().getValue() + 1;
    }

    /**
     * Limpia espacios, guiones y convierte a mayúsculas una cadena de patente.
     *
     * @param patente Cadena original ingresada.
     * @return Patente normalizada.
     */
    // Método 'static' utilitario: puede invocarse como Vehiculo.normalizarPatente(...) sin instanciar un objeto.
    // Encadena métodos de String: trim() quita espacios extremos, toUpperCase() pasa a mayúsculas
    // y replace() elimina espacios intermedios o guiones (ej: " ab-123 cd " -> "AB123CD").
    public static String normalizarPatente(String patente) {
        return patente == null ? "" : patente.trim().toUpperCase().replace(" ", "").replace("-", "");
    }

    /**
     * Verifica si una patente cumple con el formato argentino tradicional (ABC123)
     * o Mercosur (AB123CD / A123BCD).
     *
     * @param patente Patente a validar.
     * @return {@code true} si el formato es válido.
     */
    // Valida la longitud exacta con .length() y revisa posición por posición mediante bucles for y charAt()
    public static boolean esPatenteValida(String patente) {
        String p = normalizarPatente(patente);

        // Formato 1: Patente nacional tradicional de 6 caracteres (3 letras + 3 números, ej: ABC123)
        if (p.length() == 6) {
            return sonLetras(p, 0, 3) && sonDigitos(p, 3, 6);
        }

        // Formatos Mercosur de 7 caracteres en total
        if (p.length() == 7) {
            // Formato 2: Auto/Camioneta (2 letras + 3 números + 2 letras, ej: AB123CD)
            boolean mercosurAuto = sonLetras(p, 0, 2) && sonDigitos(p, 2, 5) && sonLetras(p, 5, 7);
            // Formato 3: Moto (1 letra + 3 números + 3 letras, ej: A123BCD)
            boolean mercosurMoto = sonLetras(p, 0, 1) && sonDigitos(p, 1, 4) && sonLetras(p, 4, 7);
            return mercosurAuto || mercosurMoto;
        }

        // Cualquier otra longitud es inválida
        return false;
    }

    // Método auxiliar interno: recorre el tramo [inicio, fin) con charAt(i) y verifica con la clase
    // Wrapper Character.isLetter() que todos los caracteres sean letras.
    private static boolean sonLetras(String texto, int inicio, int fin) {
        for (int i = inicio; i < fin; i++) {
            if (!Character.isLetter(texto.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    // Método auxiliar interno: recorre el tramo [inicio, fin) con charAt(i) y verifica con
    // Character.isDigit() que todos los caracteres sean dígitos numéricos (0-9).
    private static boolean sonDigitos(String texto, int inicio, int fin) {
        for (int i = inicio; i < fin; i++) {
            if (!Character.isDigit(texto.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    // =========================================================================
    // 4. MÉTODOS ABSTRACTOS (CONTRATO POLIMÓRFICO PARA LAS SUBCLASES)
    // =========================================================================

    /**
     * Devuelve la categoría general del vehículo.
     *
     * @return Tipo de vehículo implementado por la subclase.
     */
    // Método abstracto (sin llaves {}): obliga a Auto, Camioneta y Motocicleta a devolver su constante TipoVehiculo
    public abstract TipoVehiculo getTipo();

    /**
     * Devuelve la descripción textual de la carrocería o estilo específico.
     *
     * @return Nombre de la carrocería.
     */
    // Método abstracto: cada subclase resuelve cómo mostrar su propia carrocería (Enum o personalizada)
    public abstract String getCarroceria();

    // =========================================================================
    // 5. MÉTODOS ACCESORES Y MUTADORES (GETTERS Y SETTERS CON VALIDACIÓN)
    // =========================================================================

    /**
     * Obtiene la marca del vehículo.
     *
     * @return Marca del fabricante.
     */
    public String getMarca() {
        return marca;
    }

    /**
     * Establece la marca del vehículo.
     *
     * @param marca Nombre de la marca.
     * @throws DatoInvalidoException Si la marca es nula o vacía.
     */
    // Usa .trim().isEmpty() para rechazar cadenas nulas o compuestas solo por espacios en blanco
    public void setMarca(String marca) throws DatoInvalidoException {
        if (marca == null || marca.trim().isEmpty()) {
            throw new DatoInvalidoException("La marca no puede estar vacía.");
        }
        this.marca = marca.trim(); // Guarda el texto limpio sin espacios sobrantes en los extremos
    }

    /**
     * Obtiene el modelo del vehículo.
     *
     * @return Nombre del modelo.
     */
    public String getModelo() {
        return modelo;
    }

    /**
     * Establece el modelo del vehículo.
     *
     * @param modelo Nombre del modelo.
     * @throws DatoInvalidoException Si el modelo es nulo o vacío.
     */
    public void setModelo(String modelo) throws DatoInvalidoException {
        if (modelo == null || modelo.trim().isEmpty()) {
            throw new DatoInvalidoException("El modelo no puede estar vacío.");
        }
        this.modelo = modelo.trim();
    }

    /**
     * Obtiene el año de fabricación.
     *
     * @return Año del vehículo.
     */
    public Integer getAnio() {
        return anio;
    }

    /**
     * Establece el año de fabricación validando el rango permitido.
     *
     * @param anio Año a asignar.
     * @throws DatoInvalidoException Si está fuera del rango [{@value #ANIO_MINIMO}..año actual + 1].
     */
    // Valida que el año no sea nulo ni menor a ANIO_MINIMO (1950) ni mayor al año siguiente permitido
    public void setAnio(Integer anio) throws DatoInvalidoException {
        if (anio == null || anio.intValue() < ANIO_MINIMO || anio.intValue() > anioMaximo()) {
            throw new DatoInvalidoException("El año debe estar entre " + ANIO_MINIMO + " y " + anioMaximo() + ".");
        }
        this.anio = anio;
    }

    /**
     * Obtiene la patente normalizada del vehículo.
     *
     * @return Dominio del vehículo.
     */
    public String getPatente() {
        return patente;
    }

    /**
     * Establece y valida la patente del vehículo.
     *
     * @param patente Patente a registrar.
     * @throws PatenteInvalidaException Si el formato de patente no es válido.
     */
    // Normaliza la entrada y lanza la excepción específica PatenteInvalidaException (hija de DatoInvalidoException)
    public void setPatente(String patente) throws PatenteInvalidaException {
        String normalizada = normalizarPatente(patente);
        if (!esPatenteValida(normalizada)) {
            throw new PatenteInvalidaException("La patente '" + patente + "' no tiene un formato válido (ej: ABC123 o AB123CD).");
        }
        this.patente = normalizada;
    }

    /**
     * Obtiene el color del vehículo.
     *
     * @return Color exterior.
     */
    public String getColor() {
        return color;
    }

    /**
     * Establece el color del vehículo.
     *
     * @param color Color a asignar.
     * @throws DatoInvalidoException Si el color es nulo o vacío.
     */
    public void setColor(String color) throws DatoInvalidoException {
        if (color == null || color.trim().isEmpty()) {
            throw new DatoInvalidoException("El color no puede estar vacío.");
        }
        this.color = color.trim();
    }

    /**
     * Obtiene el kilometraje actual del vehículo.
     *
     * @return Kilómetros recorridos.
     */
    public Integer getKilometraje() {
        return kilometraje;
    }

    /**
     * Establece el kilometraje del vehículo.
     *
     * @param kilometraje Kilómetros recorridos (debe ser mayor o igual a 0).
     * @throws DatoInvalidoException Si el kilometraje es nulo o negativo.
     */
    // Permite 0 (unidad 0 KM) pero rechaza valores negativos
    public void setKilometraje(Integer kilometraje) throws DatoInvalidoException {
        if (kilometraje == null || kilometraje.intValue() < 0) {
            throw new DatoInvalidoException("El kilometraje no puede ser negativo.");
        }
        this.kilometraje = kilometraje;
    }

    /**
     * Obtiene el precio base de lista.
     *
     * @return Precio base sin recargos.
     */
    public Double getPrecioBase() {
        return precioBase;
    }

    /**
     * Establece el precio base de lista.
     *
     * @param precioBase Importe base positivo.
     * @throws DatoInvalidoException Si el precio es nulo o menor o igual a cero.
     */
    // Exige que el precio base sea estrictamente mayor a 0
    public void setPrecioBase(Double precioBase) throws DatoInvalidoException {
        if (precioBase == null || precioBase.doubleValue() <= 0) {
            throw new DatoInvalidoException("El precio base debe ser mayor a $0.");
        }
        this.precioBase = precioBase;
    }

    // =========================================================================
    // 6. MÉTODOS SOBRESCRITOS DE OBJECT (IDENTIDAD LÓGICA Y REPRESENTACIÓN)
    // =========================================================================

    // Define la igualdad lógica entre dos vehículos: dos instancias distintas en memoria
    // representan el mismo vehículo real si tienen exactamente la misma patente.
    @Override
    public boolean equals(Object o) {
        if (this == o) return true; // Si apuntan a la misma dirección de memoria, son iguales
        if (o == null || getClass() != o.getClass()) return false; // Si es null o de distinta clase, no son iguales
        Vehiculo vehiculo = (Vehiculo) o; // Downcasting seguro tras verificar getClass()
        return Objects.equals(this.patente, vehiculo.patente);
    }

    // Contrato obligatorio con equals(): si dos vehículos tienen la misma patente,
    // deben generar exactamente el mismo código hash para funcionar bien en colecciones (Map/Set).
    @Override
    public int hashCode() {
        return Objects.hash(this.patente);
    }

    // Redefine Object.toString() mediante concatenación estándar con '+':
    // invoca polimórficamente a getCarroceria() (implementado por cada subclase) y getEstado().
    @Override
    public String toString() {
        long precioEntero = Math.round(precioBase);
        return "[" + patente + "] " + marca + " " + modelo + " (" + anio + ")"
                + " | Color: " + color
                + " | Carrocería: " + getCarroceria()
                + " | " + getEstado() + " (" + kilometraje + " km)"
                + " | Precio Base: $" + precioEntero;
    }
}