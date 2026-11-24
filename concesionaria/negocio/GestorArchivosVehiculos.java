package concesionaria.negocio;

import concesionaria.dominio.DatoInvalidoException;
import concesionaria.dominio.Estado;
import concesionaria.dominio.Vehiculo;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Clase de persistencia (POJO) encargada de la serialización y deserialización
 * binaria del inventario de vehículos de GyQ Concesionaria, así como de las
 * operaciones de clonado, reciclado y apareo de archivos.
 * <p>
 * Sigue estrictamente el manejo explícito de flujos ({@link ObjectInputStream} y
 * {@link ObjectOutputStream} inicializados en {@code null}, verificación y creación
 * de {@link File} dentro del bloque {@code try} y cierre seguro en {@code finally}).
 * </p>
 *
 * @author GyQ Concesionaria
 * @version 1.0
 */
public class GestorArchivosVehiculos {

    // =========================================================================
    // 1. SERIALIZACIÓN BINARIA (ESCRITURA DE OBJETOS EN DISCO)
    // =========================================================================

    /**
     * Serializa y guarda una lista de vehículos en un archivo binario.
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

            // Paso 3: Se encadenan dos flujos (Decorator pattern):
            // FileOutputStream abre el canal de bytes hacia el archivo físico,
            // y ObjectOutputStream convierte el grafo de objetos Java a secuencia de bytes.
            out = new ObjectOutputStream(new FileOutputStream(archivo));

            // Paso 4: Se envuelve en un new ArrayList<>(vehiculos) para garantizar que la colección
            // raíz sea 100% Serializable (ej. si pasaran una vista interna no serializable) y se escribe.
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

    // =========================================================================
    // 3. CLONADO DE ARCHIVOS (COPIA DE SEGURIDAD / BACKUP)
    // =========================================================================

    /**
     * <b>CLONADO DE ARCHIVOS:</b> Genera una copia exacta e independiente de un archivo
     * binario de vehículos hacia un archivo destino (respaldo/backup).
     */
    public int clonarArchivo(String rutaOrigen, String rutaDestino) throws IOException, ClassNotFoundException {
        // Lee todos los registros serializados del archivo origen y los vuelve a serializar en el destino
        List<Vehiculo> registrosOriginales = leerVehiculos(rutaOrigen);
        guardarVehiculos(rutaDestino, registrosOriginales);
        return registrosOriginales.size(); // Informa cuántos registros se respaldaron
    }

    // =========================================================================
    // 4. RECICLADO DE ARCHIVOS (DEPURACIÓN POR CRITERIO DE NEGOCIO)
    // =========================================================================

    /**
     * <b>RECICLADO DE ARCHIVOS:</b> Depura un archivo binario generando un nuevo archivo
     * limpio que conserva únicamente los vehículos que cumplen con el estado comercial
     * requerido.
     */
    public int reciclarArchivoPorEstado(String rutaOrigen, String rutaReciclado, Estado estadoConservar)
            throws IOException, ClassNotFoundException {
        // Paso 1: Deserializa todos los vehículos del archivo original
        List<Vehiculo> todos = leerVehiculos(rutaOrigen);
        List<Vehiculo> reciclados = new ArrayList<>();

        // Paso 2: Recorre y selecciona únicamente aquellos que coinciden con el Enum estadoConservar (ej. NUEVO)
        for (Vehiculo v : todos) {
            if (v != null && v.getEstado() == estadoConservar) {
                reciclados.add(v);
            }
        }

        // Paso 3: Graba el subconjunto depurado en un nuevo archivo binario sin alterar el original
        guardarVehiculos(rutaReciclado, reciclados);
        return reciclados.size();
    }

    // =========================================================================
    // 5. APAREO DE ARCHIVOS (SINCRONIZACIÓN MAESTRO + NOVEDADES)
    // =========================================================================

    /**
     * <b>APAREO DE ARCHIVOS:</b> Combina y sincroniza un archivo maestro de inventario con un
     * archivo de novedades/movimientos, generando un archivo maestro actualizado sin duplicados.
     */
    public int aparearArchivos(String rutaMaestro, String rutaNovedades, String rutaActualizado)
            throws IOException, ClassNotFoundException, DatoInvalidoException {
        // Paso 1: Recupera ambos archivos binarios a memoria
        List<Vehiculo> listaMaestro = leerVehiculos(rutaMaestro);
        List<Vehiculo> listaNovedades = leerVehiculos(rutaNovedades);

        // Paso 2: Utiliza un LinkedHashMap indexado por patente (clave primaria) para fusionar sin duplicados
        Map<String, Vehiculo> mapaConsolidado = new LinkedHashMap<>();

        // Paso 3: Carga primero todo el inventario maestro actual
        for (Vehiculo v : listaMaestro) {
            if (v != null) {
                mapaConsolidado.put(v.getPatente(), v);
            }
        }

        // Paso 4: Aplica las novedades sobre el mismo mapa:
        // - Si la patente YA existía en el maestro, mapa.put() sobrescribe el registro (actualiza precio/datos).
        // - Si la patente NO existía, mapa.put() inserta el nuevo ingreso al final.
        for (Vehiculo novedad : listaNovedades) {
            if (novedad == null) {
                throw new DatoInvalidoException("Registro nulo encontrado en el archivo de novedades.");
            }
            mapaConsolidado.put(novedad.getPatente(), novedad);
        }

        // Paso 5: Convierte los valores consolidados a List y los serializa en el archivo resultante
        List<Vehiculo> resultadoFinal = new ArrayList<>(mapaConsolidado.values());
        guardarVehiculos(rutaActualizado, resultadoFinal);
        return resultadoFinal.size();
    }
}