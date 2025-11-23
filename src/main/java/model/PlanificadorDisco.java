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
    

    public PlanificadorDisco(String politica) {
        this.politica = politica.toUpperCase();
       
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
            // Actualizar cabezal al primer bloque del archivo
            int bloqueObjetivo = obtenerPrimerBloque(disco, proceso.getArchivoObjetivo());
            disco.setPosicionCabezal(bloqueObjetivo);
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
            int distancia = Math.abs(bloqueObjetivo - posicionActual);
            
            if (distancia < distanciaMin) {
                distanciaMin = distancia;
                seleccionado = p;
            }
        }
        if (seleccionado != null) {
            solicitudes.remove(seleccionado);
            int nuevoBloque = obtenerPrimerBloque(disco, seleccionado.getArchivoObjetivo());
            disco.setPosicionCabezal(nuevoBloque); 
            
        }
        return seleccionado;
    }
   // Scan y si es true circular C-SCAN
     private Proceso atenderSCAN(Cola solicitudes, Disco disco,boolean circular) {
        int posicionActual = disco.getPosicionCabezal();
        int totalBloques = disco.getTotalBloques();
        Proceso seleccionado = null;
        int menorDiferencia = Integer.MAX_VALUE;

        // Buscar proceso hacia adelante (bloque >= cabezal)
        for (int i = 0; i < solicitudes.size(); i++) {
            Proceso p = solicitudes.get(i);
            int bloqueObjetivo = obtenerPrimerBloque(disco, p.getArchivoObjetivo());
            if (bloqueObjetivo >= posicionActual) {
                int diferencia = bloqueObjetivo - posicionActual;
                if (diferencia < menorDiferencia) {
                    menorDiferencia = diferencia;
                    seleccionado = p;
                }
            }
        }

        // Si no hay más hacia adelante
        if (seleccionado == null) {
            if (circular) {
                // C-SCAN: saltar al inicio
                disco.setPosicionCabezal(0);
                return atenderSCAN(solicitudes, disco, true);
            } else {
                // SCAN: moverse hacia atrás
                for (int i = 0; i < solicitudes.size(); i++) {
                    Proceso p = solicitudes.get(i);
                    int bloqueObjetivo = obtenerPrimerBloque(disco, p.getArchivoObjetivo());
                    if (bloqueObjetivo <= posicionActual) {
                        int diferencia = posicionActual - bloqueObjetivo;
                        if (diferencia < menorDiferencia) {
                            menorDiferencia = diferencia;
                            seleccionado = p;
                        }
                    }
                }
            }
        }

        if (seleccionado != null) {
            solicitudes.remove(seleccionado);
            disco.setPosicionCabezal(obtenerPrimerBloque(disco, seleccionado.getArchivoObjetivo()));
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
        return 0; // Si no lo encuentra, asumir bloque 0
    }

    public String getPolitica() {
        return politica;
    }

    

    public void setPolitica(String politica) {
        this.politica = politica;
    }









}
