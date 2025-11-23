package model;

import edd.*;
import java.util.Random;

/**
 *
 * @author sarazo
 */
public class SistemaArchivos {
    private Directorio raiz;
    private Disco disco;
    private Cola procesos;
    private Cola solicitudesES;
   // private Buffer buffer;
    private PlanificadorDisco planificador;
    private boolean modoAdministrador;
    private String usuarioActual;
    private final int TOTAL_BLOQUES = 100; // Ejemplo: 100 bloques
    
    public SistemaArchivos() {
        this.raiz = new Directorio("root", "admin", null);
        this.disco = new Disco(TOTAL_BLOQUES);
        this.procesos = new Cola();
        this.solicitudesES = new Cola();
        //this.buffer = new Buffer(10, "FIFO"); // Buffer de 10 bloques
        this.planificador = new PlanificadorDisco("FIFO"); 
        this.modoAdministrador = true;
        this.usuarioActual = "admin";
        
       
    }
    
    // Métodos CRUD para archivos y directorios
    public boolean crearArchivo(String nombre, int tamañoBloques, Directorio directorio) {
        if (!modoAdministrador && !directorio.getPropietario().equals(usuarioActual)) {
            return false; // Sin permisos
        }
        
        if (!disco.HayEspacio(tamañoBloques)) {
            return false; // No hay espacio
        }
        
        Archivo nuevoArchivo = new Archivo(nombre, usuarioActual, tamañoBloques);
        
        ListaEnlazada bloquesAsignados = disco.asignarBloquesEncadenados(tamañoBloques, nombre);
        
        if (bloquesAsignados != null){
            nuevoArchivo.setBloquesAsignados(bloquesAsignados);
            if (bloquesAsignados.tamaño()>0) {
                nuevoArchivo.setPrimerBloque((int) bloquesAsignados.obtener(0));
            }
            directorio.agregarElemento(nuevoArchivo);
            //crear proceso para la operacion
            Proceso proceso = new Proceso("Crear_" + nombre, "CREAR", nombre, usuarioActual);
            procesos.encolar(proceso);
            agregarSolicitudES(proceso);
            return true;
        }
        return false;
    }
    
    public boolean crearDirectorio(String nombre, Directorio directorioPadre) {
        if (!modoAdministrador && !directorioPadre.getPropietario().equals(usuarioActual)) {
            return false;
        }
        
        Directorio nuevoDir = new Directorio(nombre, usuarioActual, directorioPadre);
        directorioPadre.agregarElemento(nuevoDir);
        return true;
    }
    
        public boolean eliminarArchivo(String nombreArchivo, Directorio directorio) {
        FileSystemElement elemento = directorio.buscarElemento(nombreArchivo);
        if (elemento != null && !elemento.esDirectorio()) {
            Archivo archivo = (Archivo) elemento;
            
            // Verificar permisos
            if (!modoAdministrador && !archivo.getPropietario().equals(usuarioActual)) {
                return false;
            }
            
            // Liberar bloques
            disco.liberarBloquesEncadenados(archivo.getBloquesAsignados());
            directorio.eliminarElemento(archivo);
            
            // Crear proceso para la operación
            Proceso proceso = new Proceso("Eliminar_" + nombreArchivo, "ELIMINAR", nombreArchivo, usuarioActual);
            procesos.encolar(proceso);
            agregarSolicitudES(proceso);
            
            return true;
        }
        return false;
    }
    
        public boolean eliminarDirectorio(String nombreDirectorio, Directorio directorioPadre) {
        FileSystemElement elemento = directorioPadre.buscarElemento(nombreDirectorio);
        if (elemento != null && elemento.esDirectorio()) {
            Directorio directorio = (Directorio) elemento;
            
            // Verificar permisos
            if (!modoAdministrador && !directorio.getPropietario().equals(usuarioActual)) {
                return false;
            }
            
            // Eliminar recursivamente todo el contenido
            eliminarDirectorioRecursivo(directorio);
            directorioPadre.eliminarElemento(directorio);
            
            Proceso proceso = new Proceso("Eliminar_" + nombreDirectorio, "ELIMINAR", nombreDirectorio, usuarioActual);
            procesos.encolar(proceso);
            agregarSolicitudES(proceso);
            
            return true;
        }
        return false;
    }
        
         private void eliminarDirectorioRecursivo(Directorio directorio) {
        for (int i = 0; i < directorio.getElementos().tamaño(); i++) {
            FileSystemElement elemento = (FileSystemElement) directorio.getElementos().obtener(i);
            if (elemento.esDirectorio()) {
                eliminarDirectorioRecursivo((Directorio) elemento);
            } else {
                Archivo archivo = (Archivo) elemento;
                disco.liberarBloquesEncadenados(archivo.getBloquesAsignados());
            }
        }
    }
    // Métodos para planificación de disco
    public void agregarSolicitudES(Proceso proceso) {
        solicitudesES.encolar(proceso);
        proceso.setEstado("BLOQUEADO");
    }
    
  public Proceso atenderSolicitudES() {
        Proceso proceso = planificador.atenderSolicitud(solicitudesES, disco);
        if (proceso != null) {
            proceso.setEstado("EJECUTANDO");
            
            // DEBUG: Mostrar información de la operación
        System.out.println("  [DEBUG] Atendiendo: " + proceso.getName() + 
                         " | Cabezal en: " + disco.getPosicionCabezal() +
                         " | Política: " + planificador.getPolitica());
        
            proceso.incrementarTiempo();
            proceso.setEstado("TERMINADO");
        }
     
        return proceso;
    }
  
   public Archivo buscarArchivo(String ruta) {
        String[] partes = ruta.split("/");
        Directorio actual = raiz;
        
        for (int i = 0; i < partes.length - 1; i++) {
            if (!partes[i].isEmpty()) {
                FileSystemElement elemento = actual.buscarElemento(partes[i]);
                if (elemento == null || !elemento.esDirectorio()) {
                    return null;
                }
                actual = (Directorio) elemento;
            }
        }
        
        FileSystemElement elemento = actual.buscarElemento(partes[partes.length - 1]);
        return (elemento != null && !elemento.esDirectorio()) ? (Archivo) elemento : null;
    }
       
    // Getters y setters
    public Directorio getRaiz() {
        return raiz;
    }
    

    
    public Cola getProcesos() {
        return procesos;
    }
    
    public Cola getSolicitudesES() {
        return solicitudesES;
    }
    
    //public Buffer getBuffer() {
   //     return buffer;
    //}

    public Disco getDisco() {
        return disco;
    }

    public void setDisco(Disco disco) {
        this.disco = disco;
    }

    public PlanificadorDisco getPlanificador() {
        return planificador;
    }

    public void setPlanificador(PlanificadorDisco planificador) {
        this.planificador = planificador;
    }
    
   
    
    public boolean isModoAdministrador() {
        return modoAdministrador;
    }
    
    public void setModoAdministrador(boolean modo) {
        this.modoAdministrador = modo;
    }
    
    public String getUsuarioActual() {
        return usuarioActual;
    }
    
    public void setUsuarioActual(String usuario) {
        this.usuarioActual = usuario;
    }
    
    public int getTotalBloques() {
        return TOTAL_BLOQUES;
    }
}
