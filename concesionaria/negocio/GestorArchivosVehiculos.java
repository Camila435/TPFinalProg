package concesionaria.negocio;

import concesionaria.dominio.Vehiculo;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase de persistencia (POJO) encargada de la serialización y deserialización
 * binaria del inventario de vehículos de GyQ Concesionaria.
 * <p>
 * Sigue estrictamente el manejo explícito de flujos ({@link ObjectInputStream} y
 * {@link ObjectOutputStream} inicializados en {@code null}, verificación y creación
 * de {@link File} dentro del bloque {@code try} y cierre seguro en {@code finally}).
 * </p>
 *
 * @author Camila Quintero
 * @author Guadalupe Gallardo
 * @version 1.1
 */
public class GestorArchivosVehiculos {

    // =========================================================================
    // 1. SERIALIZACIÓN BINARIA (ESCRITURA DE OBJETOS EN DISCO)
    // =========================================================================

    /**
     * Serializa y guarda una lista de vehículos en un archivo binario.
     *
     * @param rutaArchivo Ruta o nombre del archivo en disco (ej. "inventario_maestro.dat").
     * @param vehiculos   Lista de vehículos a persistir.
     * @throws IOException Si ocurre un error de entrada/salida al escribir en disco.
     */
    public void guardarVehiculos(String rutaArchivo, List<Vehiculo> vehiculos) throws IOException {
        // Paso 1: Se declara el flujo de salida de objetos en 'null' ANTES del bloque try,
        // para que sea visible tanto dentro del try como dentro del bloque finally.
        ObjectOutputStream out = null;
        try {
            // Paso 2: Se crea el descriptor lógico del archivo (File) y, si no existe en disco, se crea vacío.
            File archivo = new File(rutaArchivo);
            if (!archivo.exists()) {
                archivo.createNewFile();
            }

            // Paso 3: Se encadenan dos flujos (patrón Decorator):
            // FileOutputStream abre el canal de bytes hacia el archivo físico,
            // y ObjectOutputStream convierte los objetos Java en secuencia de bytes.
            out = new ObjectOutputStream(new FileOutputStream(archivo));

            // Paso 4: Se envuelve en un new ArrayList<>(vehiculos) para garantizar que la colección
            // raíz sea 100% Serializable y se graba completa en disco.
            out.writeObject(new ArrayList<>(vehiculos));
            out.flush(); // Fuerza el vaciado del búfer en memoria hacia el disco duro
        } finally {
            // Paso 5: Bloque finally (se ejecuta SIEMPRE, haya o no ocurrido un error en el try).
            // Garantiza el cierre explícito del flujo para no dejar el archivo bloqueado en el sistema operativo.
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    System.err.println("Error al cerrar ObjectOutputStream: " + e.getMessage());
                }
            }
        }
    }

    // =========================================================================
    // 2. DESERIALIZACIÓN BINARIA (LECTURA DE OBJETOS DESDE DISCO)
    // =========================================================================

    /**
     * Lee y deserializa la lista de vehículos almacenada en un archivo binario.
     *
     * @param rutaArchivo Ruta o nombre del archivo en disco a leer.
     * @return Lista de vehículos recuperada (o lista vacía si el archivo es nuevo).
     * @throws IOException            Si ocurre un error al leer el archivo.
     * @throws ClassNotFoundException Si no se encuentra la clase de los objetos serializados.
     */
    @SuppressWarnings("unchecked") // Suprime la advertencia de compilador por el cast genérico (List<Vehiculo>)
    public List<Vehiculo> leerVehiculos(String rutaArchivo) throws IOException, ClassNotFoundException {
        // Paso 1: Flujo de entrada de objetos inicializado en 'null' fuera del try
        ObjectInputStream in = null;
        try {
            // Paso 2: Verifica existencia del archivo; si no existe, lo crea y retorna lista vacía sin romper
            File archivo = new File(rutaArchivo);
            if (!archivo.exists()) {
                archivo.createNewFile();
                return new ArrayList<>();
            }
            // Paso 3: Si el archivo existe pero tiene 0 bytes (recién creado), evita EOFException al leer cabecera
            if (archivo.length() == 0) {
                return new ArrayList<>();
            }

            // Paso 4: Abre el flujo de lectura binaria y reconstruye los objetos en memoria RAM
            in = new ObjectInputStream(new FileInputStream(archivo));
            Object leido = in.readObject(); // Devuelve un Object genérico deserializado

            // Paso 5: Verifica con 'instanceof' que el objeto leído sea efectivamente una List antes de castear
            if (leido instanceof List<?>) {
                return (List<Vehiculo>) leido;
            }
            return new ArrayList<>();
        } finally {
            // Paso 6: Cierra el flujo de entrada de forma segura sin importar cómo terminó el método
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    System.err.println("Error al cerrar ObjectInputStream: " + e.getMessage());
                }
            }
        }
    }
}