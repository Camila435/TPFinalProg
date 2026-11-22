package negocio;

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

    /**
     * Serializa y guarda una lista de vehículos en un archivo binario.
     * Inicializa {@code ObjectOutputStream} en {@code null} antes del {@code try},
     * verifica/crea el archivo dentro del {@code try} y cierra el flujo en {@code finally}.
     *
     * @param rutaArchivo Ruta del archivo binario destino.
     * @param vehiculos   Lista de vehículos a serializar.
     * @throws IOException Si ocurre un error de entrada/salida al crear o escribir el archivo.
     */
    public void guardarVehiculos(String rutaArchivo, List<Vehiculo> vehiculos) throws IOException {
        ObjectOutputStream out = null;
        try {
            File archivo = new File(rutaArchivo);
            if (!archivo.exists()) {
                archivo.createNewFile();
            }
            out = new ObjectOutputStream(new FileOutputStream(archivo));
            out.writeObject(new ArrayList<>(vehiculos));
            out.flush();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    System.err.println("Error al cerrar ObjectOutputStream: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Lee y deserializa la lista de vehículos almacenada en un archivo binario.
     * Si el archivo no existe, lo crea vacío dentro del bloque {@code try} y devuelve
     * una lista vacía.
     *
     * @param rutaArchivo Ruta del archivo binario origen.
     * @return Lista de vehículos recuperados desde el archivo.
     * @throws IOException            Si ocurre un error de lectura.
     * @throws ClassNotFoundException Si la clase serializada no coincide con las del dominio.
     */
    @SuppressWarnings("unchecked")
    public List<Vehiculo> leerVehiculos(String rutaArchivo) throws IOException, ClassNotFoundException {
        ObjectInputStream in = null;
        try {
            File archivo = new File(rutaArchivo);
            if (!archivo.exists()) {
                archivo.createNewFile();
                return new ArrayList<>();
            }
            if (archivo.length() == 0) {
                return new ArrayList<>();
            }
            in = new ObjectInputStream(new FileInputStream(archivo));
            Object leido = in.readObject();
            if (leido instanceof List<?>) {
                return (List<Vehiculo>) leido;
            }
            return new ArrayList<>();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    System.err.println("Error al cerrar ObjectInputStream: " + e.getMessage());
                }
            }
        }
    }

    /**
     * <b>CLONADO DE ARCHIVOS:</b> Genera una copia exacta e independiente de un archivo
     * binario de vehículos hacia un archivo destino (respaldo/backup).
     *
     * @param rutaOrigen  Ruta del archivo binario maestro a clonar.
     * @param rutaDestino Ruta del archivo clonado de respaldo.
     * @return Cantidad de registros clonados.
     * @throws IOException            Si falla la lectura o escritura en disco.
     * @throws ClassNotFoundException Si falla la deserialización de los objetos.
     */
    public int clonarArchivo(String rutaOrigen, String rutaDestino) throws IOException, ClassNotFoundException {
        List<Vehiculo> registrosOriginales = leerVehiculos(rutaOrigen);
        guardarVehiculos(rutaDestino, registrosOriginales);
        return registrosOriginales.size();
    }

    /**
     * <b>RECICLADO DE ARCHIVOS:</b> Depura un archivo binario generando un nuevo archivo
     * limpio que conserva únicamente los vehículos que cumplen con el estado comercial
     * requerido (por ejemplo, reciclar solo unidades {@link Estado#NUEVO} o vigentes,
     * descartando el resto).
     *
     * @param rutaOrigen      Ruta del archivo binario original.
     * @param rutaReciclado   Ruta del archivo depurado resultante.
     * @param estadoConservar Estado comercial de las unidades que permanecerán activas.
     * @return Cantidad de vehículos conservados tras el reciclado.
     * @throws IOException            Si ocurre un error de acceso a archivos.
     * @throws ClassNotFoundException Si falla la deserialización.
     */
    public int reciclarArchivoPorEstado(String rutaOrigen, String rutaReciclado, Estado estadoConservar)
            throws IOException, ClassNotFoundException {
        List<Vehiculo> todos = leerVehiculos(rutaOrigen);
        List<Vehiculo> reciclados = new ArrayList<>();

        for (Vehiculo v : todos) {
            if (v != null && v.getEstado() == estadoConservar) {
                reciclados.add(v);
            }
        }
        guardarVehiculos(rutaReciclado, reciclados);
        return reciclados.size();
    }

    /**
     * <b>APAREO DE ARCHIVOS:</b> Combina y sincroniza un archivo maestro de inventario con un
     * archivo de novedades/movimientos (altas nuevas o actualizaciones de unidades existentes
     * identificadas por patente), generando un archivo maestro actualizado sin duplicados.
     *
     * @param rutaMaestro     Ruta del archivo maestro actual.
     * @param rutaNovedades   Ruta del archivo con ingresos o actualizaciones.
     * @param rutaActualizado Ruta del archivo maestro consolidado resultante.
     * @return Total de vehículos en el archivo maestro actualizado.
     * @throws IOException            Si ocurre un error de entrada/salida.
     * @throws ClassNotFoundException Si falla la deserialización.
     * @throws DatoInvalidoException  Si algún registro de novedades es inválido.
     */
    public int aparearArchivos(String rutaMaestro, String rutaNovedades, String rutaActualizado)
            throws IOException, ClassNotFoundException, DatoInvalidoException {
        List<Vehiculo> listaMaestro = leerVehiculos(rutaMaestro);
        List<Vehiculo> listaNovedades = leerVehiculos(rutaNovedades);

        Map<String, Vehiculo> mapaConsolidado = new LinkedHashMap<>();

        for (Vehiculo v : listaMaestro) {
            if (v != null) {
                mapaConsolidado.put(v.getPatente(), v);
            }
        }

        // Las novedades incorporan patentes nuevas o actualizan las ya existentes en el maestro
        for (Vehiculo novedad : listaNovedades) {
            if (novedad == null) {
                throw new DatoInvalidoException("Registro nulo encontrado en el archivo de novedades.");
            }
            mapaConsolidado.put(novedad.getPatente(), novedad);
        }

        List<Vehiculo> resultadoFinal = new ArrayList<>(mapaConsolidado.values());
        guardarVehiculos(rutaActualizado, resultadoFinal);
        return resultadoFinal.size();
    }
}