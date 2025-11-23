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
        
        System.out.println("=== SISTEMA DE ARCHIVOS CON PERSISTENCIA ===");
        
        // PRUEBA DE PERSISTENCIA
        probarPersistencia();
        
        // Tu código original continúa aquí
        System.out.println("\n\n=== PRUEBA COMPLETA DEL SISTEMA ===");
        
        // Cargar sistema (si existe) o crear nuevo
        SistemaArchivos sistema = PersistenciaManager.cargarEstado();
        
        MainWindow ventana = new MainWindow();
        ventana.setVisible(true);
        // Mostrar estructura
        System.out.println("\n=== ESTRUCTURA DEL SISTEMA ===");
   

        // Si es un sistema nuevo, crear estructura básica
        if (sistema.getRaiz().getElementos().tamaño() == 0) {
            System.out.println("Inicializando sistema nuevo...");
            inicializarSistema(sistema);
        }
        
        // Mostrar estructura cargada
        System.out.println("\n=== ESTRUCTURA DEL SISTEMA ===");
        mostrarEstructura(sistema.getRaiz(), 0);

        // Mostrar estado del disco
        System.out.println("\n=== ESTADO DEL DISCO ===");
        Disco disco = sistema.getDisco();
        System.out.println("Bloques totales: " + disco.getTotalBloques());
        System.out.println("Bloques libres: " + disco.getBloquesLibres());
        System.out.println("Bloques ocupados: " + (disco.getTotalBloques() - disco.getBloquesLibres()));
        
        // Mostrar archivos con sus bloques
        System.out.println("\n=== ARCHIVOS Y SUS BLOQUES ===");
        Directorio documentos = (Directorio) sistema.getRaiz().buscarElemento("Documentos");
        Directorio imagenes = (Directorio) sistema.getRaiz().buscarElemento("Imagenes");
        if (documentos != null) mostrarArchivosConBloques(documentos);
        if (imagenes != null) mostrarArchivosConBloques(imagenes);
        
        // Probar planificadores
        System.out.println("\n=== PRUEBA DE PLANIFICADORES ===");
        probarPlanificadores(sistema);
        
        // GUARDAR ESTADO AL FINAL
        System.out.println("\n=== GUARDANDO ESTADO ===");
        PersistenciaManager.guardarEstado(sistema);
        System.out.println("✅ Estado del sistema guardado para la próxima ejecución");
    }
    
    private static void probarPersistencia() {
        System.out.println("\n🔍 TEST DE PERSISTENCIA");
        
        // Crear sistema de prueba
        SistemaArchivos sistemaTest = new SistemaArchivos();
        sistemaTest.setUsuarioActual("usuario_test");
        sistemaTest.setModoAdministrador(false);
        sistemaTest.setPlanificador(new PlanificadorDisco("SSTF"));
        sistemaTest.getDisco().setPosicionCabezal(25);
        
        // Crear estructura de prueba - CON VERIFICACIÓN
        Directorio raiz = sistemaTest.getRaiz();
        System.out.println("Creando directorio TestPersistencia...");
        boolean dirCreado = sistemaTest.crearDirectorio("TestPersistencia", raiz);
        System.out.println("Directorio creado: " + dirCreado);
        
        if (dirCreado) {
            Directorio testDir = (Directorio) raiz.buscarElemento("TestPersistencia");
            System.out.println("Directorio encontrado: " + (testDir != null));
            
            if (testDir != null) {
                System.out.println("Creando archivos...");
                boolean archivo1Creado = sistemaTest.crearArchivo("archivo1.txt", 2, testDir);
                boolean archivo2Creado = sistemaTest.crearArchivo("archivo2.dat", 3, testDir);
                System.out.println("Archivo1 creado: " + archivo1Creado);
                System.out.println("Archivo2 creado: " + archivo2Creado);
                
                System.out.println("Sistema creado:");
                mostrarEstructura(raiz, 0);
                
                // Guardar
                System.out.println("\n💾 Guardando estado...");
                PersistenciaManager.guardarEstado(sistemaTest);
                
                // Cargar en nuevo sistema
                System.out.println("\n📂 Cargando estado...");
                SistemaArchivos sistemaCargado = PersistenciaManager.cargarEstado();
                
                System.out.println("Sistema cargado:");
                mostrarEstructura(sistemaCargado.getRaiz(), 0);
                System.out.println("Usuario: " + sistemaCargado.getUsuarioActual());
                System.out.println("Modo admin: " + sistemaCargado.isModoAdministrador());
                System.out.println("Política: " + sistemaCargado.getPlanificador().getPolitica());
                System.out.println("Cabezal: " + sistemaCargado.getDisco().getPosicionCabezal());
                
                System.out.println("✅ Test de persistencia completado exitosamente");
            } else {
                System.out.println("❌ Error: No se pudo encontrar el directorio recién creado");
            }
        } else {
            System.out.println("❌ Error: No se pudo crear el directorio TestPersistencia");
        }
    }
    
    private static void inicializarSistema(SistemaArchivos sistema) {
        Directorio raiz = sistema.getRaiz();
        
        // Crear estructura básica con verificación
        System.out.println("Creando estructura básica...");
        
        boolean docCreado = sistema.crearDirectorio("Documentos", raiz);
        boolean imgCreado = sistema.crearDirectorio("Imagenes", raiz);
        boolean musCreado = sistema.crearDirectorio("Musica", raiz);
        
        System.out.println("Documentos creado: " + docCreado);
        System.out.println("Imagenes creado: " + imgCreado);
        System.out.println("Musica creado: " + musCreado);
        
        Directorio documentos = (Directorio) raiz.buscarElemento("Documentos");
        Directorio imagenes = (Directorio) raiz.buscarElemento("Imagenes");
        
        if (documentos != null) {
            // Crear algunos archivos
            sistema.crearArchivo("tesis.pdf", 5, documentos);
            sistema.crearArchivo("contrato.doc", 3, documentos);
        }
        
        if (imagenes != null) {
            sistema.crearArchivo("foto.jpg", 8, imagenes);
            sistema.crearArchivo("logo.png", 2, imagenes);
        }
    }
    
    private static void probarPlanificadores(SistemaArchivos sistema) {
        String[] politicas = {"FIFO", "SSTF", "SCAN", "C-SCAN"};
        
        for (String politica : politicas) {
            System.out.println("\n--- Probando política: " + politica + " ---");
            
            sistema.setPlanificador(new PlanificadorDisco(politica));
            sistema.getDisco().setPosicionCabezal(50);
            
            // Crear archivo de prueba
            Directorio raiz = sistema.getRaiz();
            boolean archivoCreado = sistema.crearArchivo("test_" + politica + ".tmp", 2, raiz);
            
            if (archivoCreado) {
                System.out.println("Posición inicial cabezal: " + sistema.getDisco().getPosicionCabezal());
                
                // Procesar solicitudes
                while (!sistema.getSolicitudesES().isEmpty()) {
                    Proceso p = sistema.atenderSolicitudES();
                    if (p != null) {
                        System.out.println("  Procesado: " + p.getName() + 
                                         " | Archivo: " + p.getArchivoObjetivo() +
                                         " | Cabezal ahora: " + sistema.getDisco().getPosicionCabezal());
                    }
                }
                
                // Limpiar archivo temporal
                sistema.eliminarArchivo("test_" + politica + ".tmp", raiz);
            } else {
                System.out.println("  No se pudo crear archivo de prueba");
            }
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

}
    }
}
