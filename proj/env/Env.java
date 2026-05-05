package proj.env;

import proj.ast.ASTNode;
import proj.types.*;
import proj.values.VClos;

import java.util.*;

public class Env <E>{
    Env<E> anc;
    Map<String, E> bindings;

    public Env(){
        anc = null;
        bindings = new HashMap<String,E>();
    }
    
    public Env(Env<E> ancestor){
        anc = ancestor;
        bindings = new HashMap<String,E>();
    }

    private void setBindings(Map<String, E> m) {
        this.bindings = m;
    }

    public Env<E> beginScope(){
        return new Env<E>(this);
    }
    
    public Env<E> endScope(){
        return anc;
    }

    public boolean isEmpty() {
        return bindings.isEmpty();
    }

    public Env<E> copy(boolean deep) {
        Env<E> e = new Env<>(deep ? (this.anc == null ? null : this.anc.copy(deep)) : this.anc);
        e.setBindings(new HashMap<String,E>(this.bindings));
        return e;
    }

    public void assoc(String id, E bind) {
        bindings.put(id, bind);
    }

    public E find(String id, boolean consume) {
        Env<E> curr = this;
        while (curr != null) {
            E val = curr.bindings.get(id);
            if (val != null) {
                if (consume) curr.bindings.remove(id);
                return val;
            }
            curr = curr.anc;
        }
        return null;
    }

    public void addEq(E bind) {
        String e = UUID.randomUUID().toString();
        assoc(e, bind);
    }

    public E findEq(ASTNode n) {
        Env<E> curr = this;
        while (curr != null) {
            for (Map.Entry<String, E> bind : curr.bindings.entrySet()) {
                if (bind.getValue() instanceof ASTTEq teq && teq.getTerm1().defequals(n, new Env<ASTType>()))
                    return bind.getValue();
            }
            curr = curr.anc;
        }
        return null;
    }

    public String toString() {
        String fill = "";
        for (String s : bindings.keySet())
        fill += String.format("%s: %s; ", s, (bindings.get(s) instanceof VClos) ? s : bindings.get(s).toString());
        if (bindings.size() > 0) fill = fill.substring(0, fill.length()-2);
        return String.format("[%s]", fill);
    }
}
