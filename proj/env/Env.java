package proj.env;

import proj.ast.*;
import proj.debug.Debug;
import proj.defeq.DefEq;
import proj.types.*;

import java.util.*;
import java.util.function.Predicate;
import java.util.function.Function;

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

    public Env<E> copy() {
        Env<E> e = new Env<>((this.anc == null ? null : this.anc.copy()));
        e.setBindings(new HashMap<String, E>(bindings));
        return e;
    }

    public Env<E> copy(Function<E, E> copier) {
        Env<E> e = new Env<>((this.anc == null ? null : this.anc.copy(copier)));
        Map<String, E> copiedBindings = new HashMap<String, E>();
        for (var entry : bindings.entrySet())
            copiedBindings.put(entry.getKey(), copier.apply(entry.getValue()));
        e.setBindings(copiedBindings);
        return e;
    }

    public void assoc(String id, E bind) {
        bindings.put(id, bind);
    }

    public void addEq(E t) {
        String e = UUID.randomUUID().toString();
        bindings.put(e, t);
    }

    public E find(String id) {
        Env<E> curr = this;
        while (curr != null) {
            E val = curr.bindings.get(id);
            if (val != null) return val;
            curr = curr.anc;
        }
        return null;
    }

    public Env<E> retrieveScope(String id) {
        Env<E> curr = this;
        while (curr != null) {
            if (curr.bindings.containsKey(id)) return curr;
            curr = curr.anc;
        }
        return null;
    }

    public Set<String> filterIds(Env<E> stop, Predicate<E> pred) {
        Set<String> result = new HashSet<String>();
        Env<E> curr = this;
        while (curr != stop) {
            curr.bindings.forEach((id, val) -> {
                if (pred.test(val)) result.add(id);
            });
            curr = curr.anc;
        }
        return result;
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

    public E findProof(ASTNode t1, ASTNode t2, Env<ASTType> sigma, Env<ASTType> phi) {
        Env<E> curr = this;
        while (curr != null) {
            for (Map.Entry<String, E> entry : curr.bindings.entrySet())
                if (entry.getValue() instanceof ASTTEq teq) {
                    if (teq.getTerm1() instanceof ASTId || teq.getTerm2() instanceof ASTId) continue;
                    Debug.log("Testing proof: " + entry.getValue());
                    Debug.open();
                    if ((DefEq.termdefeq(t1, teq.getTerm1(), sigma, phi, false) && DefEq.termdefeq(t2, teq.getTerm2(), sigma, phi, false))
                    || (DefEq.termdefeq(teq.getTerm2(), t1, sigma, phi, false) && DefEq.termdefeq(teq.getTerm1(), t2, sigma, phi, false)))
                        return entry.getValue();
                    Debug.close();
                    Debug.nl();
                }
            curr = curr.anc;
        }
        return null;
    }

    public ASTType unfold(ASTType t) {
        return (t instanceof ASTTId tid) ? unfold((ASTType) find(tid.getId())) : t;
    }

    public String toString() {
        String fill = "";
        for (String s : bindings.keySet())
        fill += String.format("%s: %s; ", s, bindings.get(s).toString());
        if (bindings.size() > 0) fill = fill.substring(0, fill.length()-2);
        return String.format("[%s]", fill);
    }
}
