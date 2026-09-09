package proj.src.env;

import java.util.UUID;

public class Binder<V> {
    public V val;
    public String id;

    public Binder(V v) {
        val = v;
        id = UUID.randomUUID().toString();
    }

    public V getVal() {
        return val;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return val.toString();
    }
}
