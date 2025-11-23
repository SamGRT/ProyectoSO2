package edd;

/**
 *
 * @author sarazo
 */
public class Buffer {
    private final int capacidad;
    private final ListaEnlazada bloques;
    private final String politica;
    private int hits;
    private int misses;
    
    public Buffer(int capacidad, String politica) {
        this.capacidad = capacidad;
        this.politica = politica;
        this.bloques = new ListaEnlazada();
        this.hits = 0;
        this.misses = 0;
    }
    
    public Object leer(int blockNumber) {
        //Buscar en el buffer
        for (int i = 0; i < bloques.tamaño(); i++) {
            BufferEntry entry = (BufferEntry) bloques.obtener(i);
            if (entry.getBlockNumber() == blockNumber) {
                hits++;
                actualizarLRU(entry);
                return entry.getData();
            }
        }
        misses++;
        
        Object data = simularLecturaDisco(blockNumber);
        
        if (bloques.tamaño() >= capacidad) {
            aplicarReemplazo();
        }
        
        bloques.agregar(new BufferEntry(blockNumber, data));
        return data;
    }
    
    public void escribir(int blockNumber, Object data) {
        //Buscar si ya está en el buffer
        for (int i = 0; i < bloques.tamaño(); i++) {
            BufferEntry entry = (BufferEntry) bloques.obtener(i);
            if (entry.getBlockNumber() == blockNumber) {
                entry.setData(data);
                actualizarLRU(entry);
                return;
            }
        }
        
        if(bloques.tamaño() >= capacidad) {
            aplicarReemplazo();
        }
        
        bloques.agregar(new BufferEntry(blockNumber, data));
    }
    
    private void actualizarLRU(BufferEntry entry) {
        if ("LRU".equals(politica)) {
            bloques.eliminar(bloques.tamaño() - 1); //Eliminar del final
            bloques.agregar(entry); //Agregar al inicio (como más reciente)
        }
    }
    
    private void aplicarReemplazo() {
        if ("FIFO".equals(politica) || "LRU".equals(politica)) {
            bloques.eliminar(0); //Eliminar el más antiguo
        }
        //Para LFU se necesita contar accesos
    }
    
    private Object simularLecturaDisco(int blockNumber) {
        return "Datos del bloque " + blockNumber;
    }
    
    public double getHitRatio() {
        int total = hits + misses;
        return total == 0 ? 0 : (double) hits / total;
    }
    
    public ListaEnlazada getBloques() {
        return bloques;
    }
}

class BufferEntry {
    private int blockNumber;
    private Object data;
    private int accessCount;
    
    public BufferEntry(int blockNumber, Object data) {
        this.blockNumber = blockNumber;
        this.data = data;
        this.accessCount = 1;
    }
    
    public int getBlockNumber() {
        return blockNumber;
    }
    
    public Object getData() {
        return data;
    }
    
    public void setData(Object data) {
        this.data = data;
    }
    
    public int getAccessCount() {
        return accessCount;
    }
    
    public void incrementAccessCount() {
        accessCount++;
    }
}
