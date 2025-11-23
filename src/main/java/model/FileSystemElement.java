package model;

/**
 *
 * @author sarazo
 */
public abstract class FileSystemElement {
    protected String nombre;
    protected String propietario;
    protected String permisos;
    protected long fechaCreacion;
    protected long fechaModificacion;
    
    public FileSystemElement(String nombre, String propietario) {
        this.nombre = nombre;
        this.propietario = propietario;
        this.permisos = "rw-r--r--";
        this.fechaCreacion = System.currentTimeMillis();
        this.fechaModificacion = this.fechaCreacion;
    }
    
    public abstract boolean esDirectorio();
    public abstract long getTamaño();
    
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
        this.fechaModificacion = System.currentTimeMillis();
    }
    
    public String getPropietario() {
        return propietario;
    }
    
    public String getPermisos() {
        return permisos;
    }
    
    public void setPermisos(String permisos) {
        this.permisos = permisos;
    }
    
    public long getFechaCreacion() {
        return fechaCreacion;
    }
    
    public long getFechaModificacion() {
        return fechaModificacion;
    }
}
