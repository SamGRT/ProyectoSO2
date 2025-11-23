/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package model;
import edd.ListaEnlazada;
import gui.MainWindow;
/**
 *
 * @author sarazo
 */
public class ProyectoSO2 {

    public static void main(String[] args) {
        
System.out.println("=== PRUEBA COMPLETA DEL SISTEMA ===");
        
        SistemaArchivos sistema = new SistemaArchivos();
        
        // Crear estructura de directorios
        Directorio raiz = sistema.getRaiz();
        sistema.crearDirectorio("Documentos", raiz);
        sistema.crearDirectorio("Imagenes", raiz);
        sistema.crearDirectorio("Musica", raiz);
        
        Directorio documentos = (Directorio) raiz.buscarElemento("Documentos");
        Directorio imagenes = (Directorio) raiz.buscarElemento("Imagenes");
        
        // Crear archivos en diferentes directorios
        sistema.crearArchivo("tesis.pdf", 5, documentos);
        sistema.crearArchivo("contrato.doc", 3, documentos);
        sistema.crearArchivo("foto.jpg", 8, imagenes);
        sistema.crearArchivo("logo.png", 2, imagenes);
        MainWindow ventana = new MainWindow();
        ventana.setVisible(true);
        // Mostrar estructura
        /*System.out.println("\n=== ESTRUCTURA DEL SISTEMA ===");
        mostrarEstructura(raiz, 0);
        
        // Mostrar estado del disco
        System.out.println("\n=== ESTADO DEL DISCO ===");
        Disco disco = sistema.getDisco();
        System.out.println("Bloques totales: " + disco.getTotalBloques());
        System.out.println("Bloques libres: " + disco.getBloquesLibres());
        System.out.println("Bloques ocupados: " + (disco.getTotalBloques() - disco.getBloquesLibres()));
        
        // Mostrar archivos con sus bloques
        System.out.println("\n=== ARCHIVOS Y SUS BLOQUES ===");
        mostrarArchivosConBloques(documentos);
        mostrarArchivosConBloques(imagenes);
        
        // Probar eliminación
        System.out.println("\n=== PRUEBA DE ELIMINACIÓN ===");
        boolean eliminado = sistema.eliminarArchivo("contrato.doc", documentos);
        System.out.println("Archivo 'contrato.doc' eliminado: " + eliminado);
        System.out.println("Bloques libres después de eliminar: " + disco.getBloquesLibres());
        
        // Probar procesos y E/S
        System.out.println("\n=== PROCESOS Y SOLICITUDES E/S ===");
        System.out.println("Procesos en cola: " + sistema.getProcesos().size());
        System.out.println("Solicitudes E/S: " + sistema.getSolicitudesES().size());
        
        String[] politicas = {"FIFO", "SSTF", "SCAN", "C-SCAN"};
        for (String politica : politicas) {
    System.out.println("\n===============================");
    System.out.println("🔧 POLÍTICA DE PLANIFICACIÓN: " + politica);
    System.out.println("===============================");

    // Reiniciar planificador y establecer posición inicial del cabezal
    sistema.setPlanificador(new PlanificadorDisco(politica));
    sistema.getDisco().setPosicionCabezal(50); // Posición inicial diferente para ver movimiento

    System.out.println("Posición inicial del cabezal: " + sistema.getDisco().getPosicionCabezal());

    // Crear archivos en diferentes posiciones del disco
    Directorio img = (Directorio) raiz.buscarElemento("Imagenes");
    
    // Crear archivo específico
    sistema.crearArchivo("temp_" + politica + ".txt", 4, img);
    
    // Procesar todas las solicitudes pendientes
    while (!sistema.getSolicitudesES().isEmpty()) {
        Proceso p = sistema.atenderSolicitudES();
        if (p != null) {
            System.out.println("Atendido: " + p.getName() +
                             " | Archivo: " + p.getArchivoObjetivo() +
                             " | Estado: " + p.getEstado() +
                             " | Cabezal ahora en: " + sistema.getDisco().getPosicionCabezal());
        }
    }

    // Limpiar el archivo temporal
    sistema.eliminarArchivo("temp_" + politica + ".txt", img);
    
    System.out.println("Cola de solicitudes vacía: " + sistema.getSolicitudesES().isEmpty());
    System.out.println("Posición final del cabezal: " + sistema.getDisco().getPosicionCabezal());
}
        }
    
    
    private static void mostrarEstructura(FileSystemElement elemento, int nivel) {
        String indent = "  ".repeat(nivel);
        
        if (elemento.esDirectorio()) {
            Directorio dir = (Directorio) elemento;
            System.out.println(indent + "📁 " + dir.getNombre() + " (Propietario: " + dir.getPropietario() + ")");
            
            for (int i = 0; i < dir.getElementos().tamaño(); i++) {
                FileSystemElement hijo = (FileSystemElement) dir.getElementos().obtener(i);
                mostrarEstructura(hijo, nivel + 1);
            }
        } else {
            Archivo archivo = (Archivo) elemento;
            System.out.println(indent + "📄 " + archivo.getNombre() + 
                " (Bloques: " + archivo.getTamañoBloques() + 
                ", Color: " + archivo.getColor() + ")");
        }
    }
    
    private static void mostrarArchivosConBloques(Directorio directorio) {
        System.out.println("\nEn directorio '" + directorio.getNombre() + "':");
        for (int i = 0; i < directorio.getElementos().tamaño(); i++) {
            FileSystemElement elemento = (FileSystemElement) directorio.getElementos().obtener(i);
            if (!elemento.esDirectorio()) {
                Archivo archivo = (Archivo) elemento;
                System.out.println("  " + archivo.getNombre() + ":");
                System.out.println("    - Bloques asignados: " + archivo.getBloquesAsignados().tamaño());
                System.out.println("    - Primer bloque: " + archivo.getPrimerBloque());
                System.out.println("    - Color: " + archivo.getColor());
                
                // Mostrar cadena de bloques
                if (archivo.getBloquesAsignados().tamaño() > 0) {
                    System.out.print("    - Cadena: ");
                    for (int j = 0; j < archivo.getBloquesAsignados().tamaño(); j++) {
                        System.out.print(archivo.getBloquesAsignados().obtener(j));
                        if (j < archivo.getBloquesAsignados().tamaño() - 1) {
                            System.out.print(" → ");
                        }
                    }
                    System.out.println();
                }
            }
        }*/
}
    }

