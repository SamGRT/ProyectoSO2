package edd;

/**
 *
 * @author sarazo
 */
public class Block {
    private int blockNumber;
    private boolean occupied;
    private String fileId;
    private int nextBlock;
    
    public Block(int blockNumber) {
        this.blockNumber = blockNumber;
        this.occupied = false;
        this.fileId = null;
        this.nextBlock = -1; //-1 indica fin de la cadena
    }
    
    public void freeBlock() {
        this.occupied = false;
        this.fileId = null;
        this.nextBlock = -1;
    }
    
    public int getBlockNumber() {
        return blockNumber;
    }
    
    public boolean isOccupied() {
        return occupied;
    }
    
    public String getFileId() {
        return fileId;
    }
    
    public int getNextBlock() {
        return nextBlock;
    }
    
    public void setOccupied(boolean occupied) {
        this.occupied = occupied;
    }
    
    public void setFileId(String fileId) {
        this.fileId = fileId;
    }
    
    public void setNextBlock(int nextBlock) {
        this.nextBlock = nextBlock;
    }
}
