/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;
import edd.Block;
import edd.ListaEnlazada;
import edd.Cola;
import javax.swing.JOptionPane;
/**
 *
 * @author Samantha
 */
public class Disco {
    private Block[] bloques;
    private int totalBloques;
    private int bloquesLibres;
    private int posicionCabezal; 

    public Disco( int totalBloques) {
        this.totalBloques = totalBloques;
        this.bloques =new Block[totalBloques];
        this.bloquesLibres = totalBloques;
        this.posicionCabezal = 0;
        
        //inicializar bloques
        for (int i = 0; i < totalBloques; i++) {
            bloques[i] = new Block(i);
        }
    }
    
    public ListaEnlazada asignarBloquesEncadenados (int cant, String archivoID){
        if (bloquesLibres < cant){
            //no hayespacio
            return null ;
        }
        
        ListaEnlazada bloquesAsignados = new ListaEnlazada();
        int nroBloquesAsignados = 0;
        int FirstBlock =-1;
        int PreviousBlock = -1;
        
        //busco los bloques libres
        for (int i = 0; i < totalBloques && nroBloquesAsignados < cant; i++) {
            if (!bloques[i].isOccupied()) {
                //config de bloque
                bloques[i].setOccupied(true);
                bloques[i].setFileId(archivoID);
                bloques[i].setNextBlock(-1);
            
            //encadenamiento
            if (PreviousBlock != -1) {
                bloques[PreviousBlock].setNextBlock(i);
            }else{
                FirstBlock= i; 
            }
            
            bloquesAsignados.agregar(i);
            PreviousBlock = i ;
            nroBloquesAsignados ++;
            bloquesLibres --;
        }
        
       }
        return nroBloquesAsignados == cant ? bloquesAsignados : null;
    }

    public void liberarBloquesEncadenados (ListaEnlazada bloquesArchivo){
        for (int i = 0; i < bloquesArchivo.tamaño(); i++) {
            int bloqueNum = (int) bloquesArchivo.obtener(i);
            bloques[bloqueNum].freeBlock();
            bloquesLibres++;
        }
    }
    
    public boolean HayEspacio(int bloquesNecesarios){
        return bloquesLibres >= bloquesNecesarios;
    }
    
    public Block[] getTodosLosBoques(){
        return bloques;
}

    public int getTotalBloques() {
        return totalBloques;
    }

    public int getBloquesLibres() {
        return bloquesLibres;
    }

    public int getPosicionCabezal() {
        return posicionCabezal;
    }

    public void setPosicionCabezal(int posicionCabezal) {
        this.posicionCabezal = posicionCabezal;
    }
    
    
}
