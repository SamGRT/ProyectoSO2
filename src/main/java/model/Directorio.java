package model;

import edd.ListaEnlazada;

/**
 *
 * @author sarazo
 */
public class Directorio extends FileSystemElement {
    private ListaEnlazada elementos;
    private Directorio directorioPadre;
    
    public Directorio(String nombre, String propietario, Directorio padre) {
        super(nombre, propietario);
        this.elementos = new ListaEnlazada();
        this.directorioPadre = padre;
    }
    
    @Override
    public boolean esDirectorio() {
        return true;
    }
    
    @Override
    public long getTamaño() {
        long tamañoTotal = 0;
        for (int i = 0; i < elementos.tamaño(); i++) {
            FileSystemElement elemento = (FileSystemElement) elementos.obtener(i);
            tamañoTotal += elemento.getTamaño();
        }
        return tamañoTotal;
    }
    
    public void agregarElemento(FileSystemElement elemento) {
        elementos.agregar(elemento);
        this.fechaModificacion = System.currentTimeMillis();
    }
    
    public void eliminarElemento(FileSystemElement elemento) {
        for (int i = 0; i < elementos.tamaño(); i++) {
            if (elementos.obtener(i).equals(elemento)) {
                elementos.eliminar(i);
                this.fechaModificacion = System.currentTimeMillis();
                return;
            }
        }
    }
    
    public FileSystemElement buscarElemento(String nombre) {
        for (int i = 0; i < elementos.tamaño(); i++) {
            FileSystemElement elemento = (FileSystemElement) elementos.obtener(i);
            if (elemento.getNombre().equals(nombre)) {
                return elemento;
            }
        }
        return null;
    }
    
    public ListaEnlazada getElementos() {
        return elementos;
    }
    
    public Directorio getDirectorioPadre() {
        return directorioPadre;
    }
}
