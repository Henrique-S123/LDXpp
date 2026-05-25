package proj.env;

import proj.ast.*;
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

    public Set<String> getScopeIds() {
        return bindings.keySet();
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

    private E search(String id, boolean consume) {
        Env<E> curr = this;
        while (curr != null) {
            E val = consume ? curr.bindings.remove(id) : curr.bindings.get(id);
            if (val != null) return val;
            curr = curr.anc;
        }
        return null;
    }

    public E find(String id) { return search(id, false); }

    public E remove(String id) { return search(id, true); }

    public Env<E> retrieveScope(String id) {
        Env<E> curr = this;
        while (curr != null) {
            if (curr.bindings.containsKey(id)) return curr;
            curr = curr.anc;
        }
        return null;
    }

    public ASTNode findEq(String id) {
        // TODO: review
        Env<E> curr = this;
        while (curr != null) {
            for (E val : curr.bindings.values())
                if (val instanceof ASTTEq teq && teq.getTerm1() instanceof ASTId nid && id.equals(nid.getId()))
                    return teq.getTerm2();
            curr = curr.anc;
        }
        curr = this;
        while (curr != null) {
            for (E val : curr.bindings.values())
                if (val instanceof ASTTEq teq && teq.getTerm2() instanceof ASTId nid && id.equals(nid.getId()))
                    return teq.getTerm1();
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
