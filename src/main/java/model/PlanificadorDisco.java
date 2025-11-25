/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;
import edd.Block;
import edd.Cola;
/**
 *
 * @author Samantha
 */
public class PlanificadorDisco {
     private String politica;
     private int posicionCabezal;
    private boolean direccionScan; //true-arriba false - abajo
    

    public PlanificadorDisco(String politica) {
        this.politica = politica.toUpperCase();
       this.posicionCabezal = 0;
        this.direccionScan = true;
    }
    
    public Proceso atenderSolicitud(Cola solicitudes, Disco disco){
        if (solicitudes.isEmpty()) {
            return null;
        }
        switch(politica.toUpperCase()) {
            case "FIFO":
                return atenderFIFO(solicitudes,disco);
            case "SSTF":
                return atenderSSTF(solicitudes, disco);
            case "SCAN":
                return atenderSCAN(solicitudes, disco,false);
            case "C-SCAN":
                return atenderSCAN(solicitudes, disco,true);
            default:
                return atenderFIFO(solicitudes,disco);
        }
    }
    
   private Proceso atenderFIFO(Cola solicitudes,Disco disco) {
        Proceso proceso = solicitudes.desencolar();
        if (proceso != null) {
            int bloqueObjetivo = obtenerPrimerBloque(disco, proceso.getArchivoObjetivo());
            if (bloqueObjetivo != -1) {
                posicionCabezal = bloqueObjetivo;
            }
        }
        return proceso;
    }
    
   
    private Proceso atenderSSTF(Cola solicitudes, Disco disco) {
        int posicionActual = disco.getPosicionCabezal();
        Proceso seleccionado= null;
        int distanciaMin =Integer.MAX_VALUE;
        
        for (int i = 0; i < solicitudes.size(); i++) {
            Proceso p= solicitudes.get(i);
            int bloqueObjetivo = obtenerPrimerBloque(disco, p.getArchivoObjetivo());
            if (bloqueObjetivo != -1) {
                int distancia = Math.abs(bloqueObjetivo - posicionActual);

                if (distancia < distanciaMin) {
                    distanciaMin = distancia;
                    seleccionado = p;
                }
            }
        }
        
        if (seleccionado != null) {
            solicitudes.remove(seleccionado);
            int nuevoBloque = obtenerPrimerBloque(disco, seleccionado.getArchivoObjetivo());
            if (nuevoBloque != -1) {
                posicionCabezal = nuevoBloque;
            }
        }
        
        // Agregar el println aquí antes del return TEMPORAL
    if (seleccionado != null) {
        int bloqueObjetivo = obtenerPrimerBloque(disco, seleccionado.getArchivoObjetivo());
        System.out.println("Evaluando " + seleccionado.getName() + " bloque objetivo: " + bloqueObjetivo);
    }
        return seleccionado;
    }
   // Scan y si es true circular C-SCAN
       private Proceso atenderSCAN(Cola solicitudes, Disco disco, boolean circular) {
        Proceso seleccionado = null;
        int mejorDistancia = Integer.MAX_VALUE;
        int totalBloques = disco.getTotalBloques();
        
        // SCAN normal
        if (direccionScan) {
            // Buscar hacia arriba
            for (int i = 0; i < solicitudes.size(); i++) {
                Proceso p = solicitudes.get(i);
                int bloque = obtenerPrimerBloque(disco, p.getArchivoObjetivo());
                if (bloque != -1 && bloque >= posicionCabezal) {
                    int distancia = bloque - posicionCabezal;
                    if (distancia < mejorDistancia) {
                        mejorDistancia = distancia;
                        seleccionado = p;
                    }
                }
            }
            
            // Si no encontró hacia arriba, cambiar dirección
            if (seleccionado == null) {
                direccionScan = false;
                // Buscar el más cercano hacia abajo
                for (int i = 0; i < solicitudes.size(); i++) {
                    Proceso p = solicitudes.get(i);
                    int bloque = obtenerPrimerBloque(disco, p.getArchivoObjetivo());
                    if (bloque != -1) {
                        int distancia = posicionCabezal - bloque;
                        if (distancia < mejorDistancia) {
                            mejorDistancia = distancia;
                            seleccionado = p;
                        }
                    }
                }
            }
        } else {
            // Buscar hacia abajo
            for (int i = 0; i < solicitudes.size(); i++) {
                Proceso p = solicitudes.get(i);
                int bloque = obtenerPrimerBloque(disco, p.getArchivoObjetivo());
                if (bloque != -1 && bloque <= posicionCabezal) {
                    int distancia = posicionCabezal - bloque;
                    if (distancia < mejorDistancia) {
                        mejorDistancia = distancia;
                        seleccionado = p;
                    }
                }
            }
            
            // Si no encontró hacia abajo, cambiar dirección
            if (seleccionado == null) {
                direccionScan = true;
                // C-SCAN: volver al inicio si es circular
                if (circular) {
                    posicionCabezal = 0;
                }
                // Buscar el más cercano hacia arriba
                for (int i = 0; i < solicitudes.size(); i++) {
                    Proceso p = solicitudes.get(i);
                    int bloque = obtenerPrimerBloque(disco, p.getArchivoObjetivo());
                    if (bloque != -1) {
                        int distancia = bloque - posicionCabezal;
                        if (distancia < mejorDistancia) {
                            mejorDistancia = distancia;
                            seleccionado = p;
                        }
                    }
                }
            }
        }
        
        if (seleccionado != null) {
            solicitudes.remove(seleccionado);
            int nuevoBloque = obtenerPrimerBloque(disco, seleccionado.getArchivoObjetivo());
            if (nuevoBloque != -1) {
                posicionCabezal = nuevoBloque;
            }
        }
        //TEMPORAAAAL
         if (seleccionado != null) {
        int bloqueObjetivo = obtenerPrimerBloque(disco, seleccionado.getArchivoObjetivo());
        System.out.println("Evaluando " + seleccionado.getName() + " bloque objetivo: " + bloqueObjetivo);
    }
        return seleccionado;
    }
    
   
    
private int obtenerPrimerBloque(Disco disco, String archivoNombre) {
        // Buscar el primer bloque del archivo en el disco
        Block[] bloques = disco.getTodosLosBoques();
        for (Block b : bloques) {
            if (b.isOccupied() && archivoNombre.equals(b.getFileId())) {
                return b.getBlockNumber();
            }
        }
        return -1; 
    }

    public String getPolitica() {
        return politica;
    }

    public int getPosicionCabezal() {
        return posicionCabezal;
    }

    public void setPosicionCabezal(int posicionCabezal) {
        this.posicionCabezal = posicionCabezal;
    }

    

    public void setPolitica(String politica) {
        this.politica = politica;
    }









}
