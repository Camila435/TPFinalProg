package negocio;

import concesionaria.dominio.Vehiculo;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase genérica encargada de gestionar el lavado y el mantenimiento
 * preventivo de los vehículos de la concesionaria.
 *
 * @param <T> Tipo de vehículo que extiende de {@link Vehiculo}.
 */
public class TallerGeneral<T extends Vehiculo> {

    private List<T> historialAtendidos;

    public TallerGeneral() {
        this.historialAtendidos = new ArrayList<>();
    }

    /**
     * Procesa un vehículo en el taller: siempre lo lava, y si es usado
     * ({@code !vehiculo.esNuevo()}), le realiza mantenimiento mecánico.
     *
     * @param vehiculo El vehículo a ingresar al taller.
     */
    public void procesarVehiculo(T vehiculo) {
        if (vehiculo == null) {
            System.out.println("Error: No se ingresó ningún vehículo al taller.");
            return;
        }

        System.out.println("\n--- Ingreso a Taller General: " + vehiculo.getPatente() + " ---");

        // 1. Lavado obligatorio para todos los vehículos (0 KM y usados)
        lavarVehiculo(vehiculo);

        // 2. Mantenimiento condicional: solo si es usado (kilometraje > 0)
        if (!vehiculo.esNuevo()) {
            realizarMantenimiento(vehiculo);
        } else {
            System.out.println("Estado 0 KM: No requiere mantenimiento mecánico.");
        }

        historialAtendidos.add(vehiculo);
    }

    private void lavarVehiculo(T vehiculo) {
        System.out.println("Lavado completo realizado para " + vehiculo.getMarca() + " " 
                + vehiculo.getModelo() + " [" + vehiculo.getPatente() + "].");
    }

    private void realizarMantenimiento(T vehiculo) {
        // Cálculo de mantenimiento basado en el kilometraje del vehículo usado
        double costoMantenimiento = 25000.0 + (vehiculo.getKilometraje() * 1.5);
        System.out.printf("Vehículo USADO (%d km): Mantenimiento realizado. Costo: $%.2f%n",
                vehiculo.getKilometraje(), costoMantenimiento);
    }

    public List<T> getHistorialAtendidos() {
        return historialAtendidos;
    }
}