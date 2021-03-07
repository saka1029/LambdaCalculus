package lambda;

public class Bind<K, V> {

    public final Bind<K, V> previous;
    public final K key;
    public final V value;

    public Bind(Bind<K, V> previous, K key, V value) {
        this.previous = previous;
        this.key = key;
        this.value = value;
    }

    @Override
    public String toString() {
        return bindToString(this);
    }

    public static <K, V> Bind<K, V> bind(Bind<K, V> previous, K key, V value) {
        return new Bind<>(previous, key, value);
    }

    public static <K, V> V get(Bind<K, V> bind, K key) {
        for (; bind != null; bind = bind.previous)
            if (bind.key.equals(key))
                return bind.value;
        return null;
    }

    public static <K, V> String toString(Bind<K, V> bind) {
        StringBuilder sb = new StringBuilder("{");
        String sep = "";
        for (; bind != null; bind = bind.previous, sep = ", ")
            sb.append(sep).append(bind.key).append("=").append(bind.value);
        return sb.append("}").toString();
    }

    public static <K, V> String bindToString(Bind<K, V> bind) {
        StringBuilder sb = new StringBuilder("{");
        String sep = "";
        for (; bind != null; bind = bind.previous, sep = ", ")
            sb.append(sep).append(bind.key).append("=").append(bind.value);
        return sb.append("}").toString();
    }
}
