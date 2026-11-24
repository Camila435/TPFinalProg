package concesionaria.negocio;

import concesionaria.dominio.Vehiculo;
import java.util.ArrayList;
import java.util.List;
/**
 * Subclase concreta (<b>JavaBean</b>) que representa una Camioneta (SUV, Pick-Up
 * o Furgón) dentro del inventario de ventas de GyQ Concesionaria.
 * <p>
 * Cumple formalmente con el estándar JavaBean: atributos estrictamente privados
 * ({@code private}), constructor público sin argumentos ({@link #Camioneta()}),
 * métodos accesores y mutadores estandarizados ({@code get}/{@code set}) e
 * implementación de {@link java.io.Serializable} heredada de {@link Vehiculo}.
 * </p>
 *
 * @author Camila Quintero
 * @author Guadalupe Gallardo
 * @version 1.2
 */
// GENÉRICOS CON ACOTACIÓN SUPERIOR (<T extends Vehiculo>):
// - <T> es un parámetro de tipo: permite crear tanto un TallerGeneral<Vehiculo> (multimarca/general),
//   como un taller especializado TallerGeneral<Motocicleta> o TallerGeneral<Camioneta> sin repetir código.
// - 'extends Vehiculo' garantiza seguridad de tipos en tiempo de compilación: impide instanciar
//   TallerGeneral<String> o TallerGeneral<Integer>, y habilita invocar métodos propios de Vehiculo
//   (como getPatente(), esNuevo() o getKilometraje()) sobre cualquier objeto de tipo T.
public class TallerGeneral<T extends Vehiculo> {

    // =========================================================================
    // 1. AMBIENTE DE ATRIBUTOS (COLECCIÓN PARAMETRIZADA)
    // =========================================================================
    // Lista genérica tipada con <T>: conserva el historial exacto de las unidades procesadas
    private List<T> historialAtendidos;

    // =========================================================================
    // 2. CONSTRUCTOR
    // =========================================================================
    public TallerGeneral() {
        // Inicializa la lista vacía usando el operador diamante (<>)
        this.historialAtendidos = new ArrayList<>();
    }

    // =========================================================================
    // 3. MÉTODO PRINCIPAL DE PROCESAMIENTO POLIMÓRFICO
    // =========================================================================

    /**
     * Procesa un vehículo en el taller: siempre lo lava, y si es usado
     * ({@code !vehiculo.esNuevo()}), le realiza mantenimiento mecánico.
     */
    public void procesarVehiculo(T vehiculo) {
        // Validación defensiva: evita NullPointerException si se pasa una referencia nula
        if (vehiculo == null) {
            System.out.println("Error: No se ingresó ningún vehículo al taller.");
            return;
        }

        // Gracias a la cota 'T extends Vehiculo', el compilador sabe que 'vehiculo' tiene getPatente()
        System.out.println("\n--- Ingreso a Taller General: " + vehiculo.getPatente() + " ---");

        // Paso 1: Lavado obligatorio para todos los vehículos (tanto 0 KM como usados)
        lavarVehiculo(vehiculo);

        // Paso 2: Lógica condicional de negocio basada en el estado comercial del vehículo:
        // esNuevo() devuelve true si kilometraje == 0 (Estado.NUEVO), o false si es usado.
        if (!vehiculo.esNuevo()) {
            realizarMantenimiento(vehiculo); // Solo los vehículos usados pasan por service mecánico
        } else {
            System.out.println("Estado 0 KM: No requiere mantenimiento mecánico.");
        }

        // Paso 3: Registra la unidad atendida en el historial genérico del taller
        historialAtendidos.add(vehiculo);
    }

    // =========================================================================
    // 4. MÉTODOS PRIVADOS AUXILIARES (ENCAPSULAMIENTO DE PASOS INTERNOS)
    // =========================================================================

    // Son 'private' porque forman parte del flujo interno de procesarVehiculo() y no deben
    // llamarse por separado desde afuera sin registrar el ingreso completo.
    private void lavarVehiculo(T vehiculo) {
        System.out.println("Lavado completo realizado para " + vehiculo.getMarca() + " " 
                + vehiculo.getModelo() + " [" + vehiculo.getPatente() + "].");
    }

    private void realizarMantenimiento(T vehiculo) {
        // Fórmula comercial: arancel base fijo ($25.000) + un adicional proporcional al desgaste ($1,50 por km)
        double costoMantenimiento = 25000.0 + (vehiculo.getKilometraje() * 1.5);
        System.out.printf("Vehículo USADO (%d km): Mantenimiento realizado. Costo: $%.2f%n",
                vehiculo.getKilometraje(), costoMantenimiento);
    }

    // =========================================================================
    // 5. ACCESOR DEL HISTORIAL
    // =========================================================================
    public List<T> getHistorialAtendidos() {
        return historialAtendidos;
    }
}