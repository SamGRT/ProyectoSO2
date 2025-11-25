/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author sarazo
 */
public class Proceso {
    private String name;
    private String estado;
    private String operacion;
    private String archivoObjetivo;
    private String usuario;
    private int tiempoEjecucion;
    
    public Proceso(String name, String operacion, String archivoObjetivo, String usuario) {
        this.name = name;
        this.estado = "NUEVO";
        this.operacion = operacion;
        this.archivoObjetivo = archivoObjetivo;
        this.usuario = usuario;
        this.tiempoEjecucion = 0;
    }
    
    public String getBloqueActual() {
        // Para simular, puedes retornar un valor o implementar lógica real
        switch(operacion) {
            case "CREAR": return "Asignando";
            case "ELIMINAR": return "Liberando";
            case "RENAME": return "N/A";
            default: return "Procesando";
        }
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Proceso proceso = (Proceso) obj;
        return name.equals(proceso.name);
    }
    
    @Override
    public int hashCode() {
        return name.hashCode();
    }
    public String getName() {
        return name;
    }
    
    public String getEstado() {
        return estado;
    }
    
    public void setEstado(String estado) {
        this.estado = estado;
    }
    
    public String getOperacion() {
        return operacion;
    }
    
    public String getArchivoObjetivo() {
        return archivoObjetivo;
    }
    
    public String getUsuario() {
        return usuario;
    }
    
    public int getTiempoEjecucion() {
        return tiempoEjecucion;
    }
    
    public void incrementarTiempo() {
        this.tiempoEjecucion++;
    }
}
