package model;

import edd.ListaEnlazada;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

/**
 *
 * @author sarazot
 */
public class PersistenciaManager {
    private static final String ARCHIVO_ESTADO = "sistema_archivos_estado.txt";
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    public static void guardarEstado(SistemaArchivos sistema) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(ARCHIVO_ESTADO))) {
            writer.println("# Estado del Sistema de Archivos - " + new Date());
            writer.println("# Formato: tipo|ruta|propietario|tamaño|permisos|fechaCreacion|fechaModificacion|bloques");
            writer.println();
            
            // Guardar configuración
            writer.println("CONFIG|modoAdministrador=" + sistema.isModoAdministrador());
            writer.println("CONFIG|usuarioActual=" + sistema.getUsuarioActual());
            writer.println("CONFIG|politicaPlanificador=" + sistema.getPlanificador().getPolitica());
            writer.println("CONFIG|posicionCabezal=" + sistema.getDisco().getPosicionCabezal());
            writer.println();
            
            // Guardar estructura
            guardarDirectorioRecursivo(sistema.getRaiz(), "", writer);
            
            System.out.println("✅ Estado guardado en: " + ARCHIVO_ESTADO);
            
        } catch (IOException e) {
            System.err.println("❌ Error al guardar: " + e.getMessage());
        }
    }
    
    public static SistemaArchivos cargarEstado() {
        File archivo = new File(ARCHIVO_ESTADO);
        if (!archivo.exists()) {
            System.out.println("ℹ️ No hay estado previo. Sistema nuevo.");
            return new SistemaArchivos();
        }
        
        SistemaArchivos sistema = new SistemaArchivos();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
            String linea;
            
            while ((linea = reader.readLine()) != null) {
                if (linea.trim().isEmpty() || linea.startsWith("#")) {
                    continue; // Saltar líneas vacías y comentarios
                }
                
                String[] partes = linea.split("\\|");
                if (partes.length < 2) continue;
                
                String tipo = partes[0];
                
                if ("CONFIG".equals(tipo)) {
                    cargarConfiguracion(partes[1], sistema);
                } else if ("DIRECTORIO".equals(tipo)) {
                    cargarDirectorio(partes, sistema);
                } else if ("ARCHIVO".equals(tipo)) {
                    cargarArchivo(partes, sistema);
                }
            }
            
            System.out.println("✅ Estado cargado desde: " + ARCHIVO_ESTADO);
            
        } catch (IOException e) {
            System.err.println("❌ Error al cargar: " + e.getMessage());
        }
        
        return sistema;
    }
    
    private static void guardarDirectorioRecursivo(Directorio directorio, String ruta, PrintWriter writer) {
        String rutaCompleta = ruta + directorio.getNombre();
        
        writer.println("DIRECTORIO|" + rutaCompleta + "|" + 
                      directorio.getPropietario() + "|" +
                      directorio.getPermisos() + "|" +
                      directorio.getFechaCreacion() + "|" +
                      directorio.getFechaModificacion());
        
        ListaEnlazada elementos = directorio.getElementos();
        for (int i = 0; i < elementos.tamaño(); i++) {
            FileSystemElement elemento = (FileSystemElement) elementos.obtener(i);
            
            if (elemento.esDirectorio()) {
                guardarDirectorioRecursivo((Directorio) elemento, rutaCompleta + "/", writer);
            } else {
                Archivo archivo = (Archivo) elemento;
                writer.println("ARCHIVO|" + rutaCompleta + "/" + archivo.getNombre() + "|" +
                              archivo.getPropietario() + "|" +
                              archivo.getTamañoBloques() + "|" +
                              archivo.getPermisos() + "|" +
                              archivo.getFechaCreacion() + "|" +
                              archivo.getFechaModificacion() + "|" +
                              bloquesToString(archivo.getBloquesAsignados()));
            }
        }
    }
    
    private static String bloquesToString(ListaEnlazada bloques) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bloques.tamaño(); i++) {
            if (i > 0) sb.append(",");
            sb.append(bloques.obtener(i));
        }
        return sb.toString();
    }
    
    private static void cargarConfiguracion(String config, SistemaArchivos sistema) {
        String[] keyValue = config.split("=");
        if (keyValue.length != 2) return;
        
        switch (keyValue[0]) {
            case "modoAdministrador":
                sistema.setModoAdministrador(Boolean.parseBoolean(keyValue[1]));
                break;
            case "usuarioActual":
                sistema.setUsuarioActual(keyValue[1]);
                break;
            case "politicaPlanificador":
                sistema.setPlanificador(new PlanificadorDisco(keyValue[1]));
                break;
            case "posicionCabezal":
                sistema.getDisco().setPosicionCabezal(Integer.parseInt(keyValue[1]));
                break;
        }
    }
    
    private static void cargarDirectorio(String[] partes, SistemaArchivos sistema) {
        if (partes.length < 6) return;
        
        String ruta = partes[1];
        String nombre = obtenerNombreDesdeRuta(ruta);
        Directorio padre = obtenerDirectorioPadre(ruta, sistema);
        
        if (padre != null) {
            Directorio nuevoDir = new Directorio(nombre, partes[2], padre);
            nuevoDir.setPermisos(partes[3]);
            padre.agregarElemento(nuevoDir);
        }
    }
    
    private static void cargarArchivo(String[] partes, SistemaArchivos sistema) {
        if (partes.length < 8) return;
        
        String ruta = partes[1];
        String nombre = obtenerNombreDesdeRuta(ruta);
        Directorio padre = obtenerDirectorioPadre(ruta, sistema);
        
        if (padre != null) {
            int tamañoBloques = Integer.parseInt(partes[3]);
            Archivo archivo = new Archivo(nombre, partes[2], tamañoBloques,padre);
            archivo.setPermisos(partes[4]);
            
            // Cargar bloques
            if (partes.length > 7) {
                String[] bloquesStr = partes[7].split(",");
                ListaEnlazada bloquesAsignados = new ListaEnlazada();
                for (String bloque : bloquesStr) {
                    if (!bloque.isEmpty()) {
                        bloquesAsignados.agregar(Integer.parseInt(bloque));
                    }
                }
                archivo.setBloquesAsignados(bloquesAsignados);
                if (bloquesAsignados.tamaño() > 0) {
                    archivo.setPrimerBloque((int) bloquesAsignados.obtener(0));
                }
            }
            
            padre.agregarElemento(archivo);
        }
    }
    
    private static String obtenerNombreDesdeRuta(String ruta) {
        String[] partesRuta = ruta.split("/");
        return partesRuta[partesRuta.length - 1];
    }
    
    public static void reconstruirArbolVisual(DefaultMutableTreeNode rootNode, DefaultTreeModel treeModel, SistemaArchivos sistema) {
    rootNode.removeAllChildren();
    reconstruirArbolRecursivo(sistema.getRaiz(), rootNode);
    treeModel.reload();
}

private static void reconstruirArbolRecursivo(Directorio directorio, DefaultMutableTreeNode parentNode) {
    ListaEnlazada elementos = directorio.getElementos();
    for (int i = 0; i < elementos.tamaño(); i++) {
        FileSystemElement elemento = (FileSystemElement) elementos.obtener(i);
        DefaultMutableTreeNode nuevoNodo = new DefaultMutableTreeNode(elemento);
        parentNode.add(nuevoNodo);
        
        if (elemento.esDirectorio()) {
            reconstruirArbolRecursivo((Directorio) elemento, nuevoNodo);
        }
    }
}
    
    private static Directorio obtenerDirectorioPadre(String ruta, SistemaArchivos sistema) {
        String[] partesRuta = ruta.split("/");
        
        if (partesRuta.length == 1) {
            return sistema.getRaiz(); // Es un directorio directamente en raíz
        }
        
        // Buscar el directorio padre
        Directorio actual = sistema.getRaiz();
        for (int i = 0; i < partesRuta.length - 1; i++) {
            if (!partesRuta[i].isEmpty()) {
                FileSystemElement elemento = actual.buscarElemento(partesRuta[i]);
                if (elemento != null && elemento.esDirectorio()) {
                    actual = (Directorio) elemento;
                } else {
                    return null; // Ruta inválida
                }
            }
        }
        return actual;
    }
    
    public static boolean existeEstadoGuardado() {
        return new File(ARCHIVO_ESTADO).exists();
    }
}