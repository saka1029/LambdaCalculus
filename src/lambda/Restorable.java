package lambda;

public interface Restorable extends AutoCloseable {
    
    @Override void close();

}
