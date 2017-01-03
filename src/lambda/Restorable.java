package lambda;

public interface Restorable extends AutoCloseable {
    
    void close();

}
