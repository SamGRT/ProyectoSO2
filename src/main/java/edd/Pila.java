/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edd;

/**
 *
 * @author sarazo
 */
public class Pila {
    private Nodo top;
    private int tamaño;
    
    private class Nodo {
        Object dato;
        Nodo siguiente;
        
        Nodo(Object dato) {
            this.dato = dato;
            this.siguiente = null;
        }
    }
    
    public Pila() {
        this.top = null;
        this.tamaño = 0;
    }
    
    public void push(Object dato) {
        Nodo nuevoNodo = new Nodo(dato);
        nuevoNodo.siguiente = top;
        top = nuevoNodo;
        tamaño++;
    }
    
    public Object pop() {
        if (estaVacia()) {
            return null;
        }
        Object dato = top.dato;
        top = top.siguiente;
        tamaño--;
        return dato;
    }
    
    public Object peek() {
        return estaVacia() ? null : top.dato;
    }
    
    public boolean estaVacia() {
        return top == null;
    }
    
    public int getTamaño() {
        return tamaño;
    }
    
    public void limpiar() {
        top = null;
        tamaño = 0;
    }
}
