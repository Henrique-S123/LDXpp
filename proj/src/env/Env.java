package proj.src.env;

import java.util.*;

public class Env<E>{
    Env<E> anc;
    Map<String, Binder<E>> bindings;

    public Env(){
        anc = null;
        bindings = new HashMap<String, Binder<E>>();
    }
    
    public Env(Env<E> ancestor){
        anc = ancestor;
        bindings = new HashMap<String, Binder<E>>();
    }

    public Env<E> beginScope(){ return new Env<E>(this); }
    
    public Env<E> endScope(){ return anc; }

    public Map<String, Binder<E>> getBindings() { return bindings; }

    public Env<E> copy() {
        Env<E> e = new Env<>((this.anc == null ? null : this.anc.copy()));
        e.bindings = new HashMap<>(bindings);
        return e;
    }

    public void assoc(String id, E val) {
        bindings.put(id, new Binder<E>(val));
    }
    
    public void assoc(String id, Binder<E> binder) {
        bindings.put(id, binder);
    }

    public String getFreshId() {
        return UUID.randomUUID().toString();
    }

    private Binder<E> findBinder(String id) {
        Env<E> curr = this;
        while (curr != null) {
            Binder<E> b = curr.bindings.get(id);
            if (b != null) return b;
            curr = curr.anc;
        }
        return null;
    }

    public E find(String id) {
        Binder<E> b = findBinder(id);
        return b == null ? null : b.val;
    }

    public String findBinderId(String id) {
        Binder<E> b = findBinder(id);
        return b == null ? null : b.id;
    }

    public String toString() {
        String fill = "";
        for (String s : bindings.keySet())
        fill += String.format("%s: %s; ", s, bindings.get(s).toString());
        if (bindings.size() > 0) fill = fill.substring(0, fill.length()-2);
        return String.format("[%s]", fill);
    }
}
