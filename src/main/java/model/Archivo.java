package model;

import edd.ListaEnlazada;

/**
 *
 * @author sarazo
 */
public class Archivo extends FileSystemElement {
    private int tamañoBloques;
    private int primerBloque;
    private ListaEnlazada bloquesAsignados;
    private String contenido;
    private String color;
    private Directorio directorioPadre;
    
    public Archivo(String nombre, String propietario, int tamañoBloques,Directorio directorioPadre) {
        super(nombre, propietario);
        this.tamañoBloques = tamañoBloques;
        this.primerBloque = -1;
        this.bloquesAsignados = new ListaEnlazada();
        this.contenido = "";
        this.color = generarColor();
        this.directorioPadre = directorioPadre; 
    }
    
    @Override
    public boolean esDirectorio() {
        return false;
    }

    public void setDirectorioPadre(Directorio directorioPadre) {
        this.directorioPadre = directorioPadre;
    }

    public Directorio getDirectorioPadre() {
        return directorioPadre;
    }
    
    @Override
    public long getTamaño() {
        return tamañoBloques * 1024L; //suponiendo 1KB por bloque
    }
    
    private String generarColor() {
        String[] colores = {"#FF6B6B", "#4ECDC4", "#45B7D1", "#96CEB4", "#FFEAA7", "#DDA0DD", "#98D8C8", "#F7DC6F"};
        return colores[Math.abs(nombre.hashCode()) % colores.length];
    }
    @Override
    public String toString() {
        return this.nombre; 
    }
    public int getTamañoBloques() {
        return tamañoBloques;
    }
    
    public int getPrimerBloque() {
        return primerBloque;
    }
    
    public void setPrimerBloque(int primerBloque) {
        this.primerBloque = primerBloque;
    }
    
    public ListaEnlazada getBloquesAsignados() {
        return bloquesAsignados;
    }
    
    public String getContenido () {
        return contenido;
    }
    
    public void setContenido(String contenido) {
        this.contenido = contenido;
    }
    
    public String getColor() {
        return color;
    }

    public void setBloquesAsignados(ListaEnlazada bloquesAsignados) {
        this.bloquesAsignados = bloquesAsignados;
    }
    
}
