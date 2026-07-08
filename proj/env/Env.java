package proj.env;

import proj.ast.*;
import proj.debug.Debug;
import proj.defeq.DefEq;
import proj.types.*;

import java.util.*;
import java.util.function.Predicate;
import java.util.function.Function;

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

    public Env<E> beginScope(){
        return new Env<E>(this);
    }
    
    public Env<E> endScope(){
        return anc;
    }

    public Env<E> copy() {
        Env<E> e = new Env<>((this.anc == null ? null : this.anc.copy()));
        e.bindings = new HashMap<>(bindings);
        return e;
    }

    public Env<E> copy(Function<E, E> copier) {
        Env<E> e = new Env<>((this.anc == null ? null : this.anc.copy(copier)));
        Map<String, Binder<E>> copiedBindings = new HashMap<String, Binder<E>>();
        for (var entry : bindings.entrySet())
            copiedBindings.put(entry.getKey(), new Binder<E>(copier.apply(entry.getValue().val)));
        e.bindings = copiedBindings;
        return e;
    }

    public void assoc(String id, E val) {
        bindings.put(id, new Binder<E>(val));
    }

    public void assoc(String id, Binder<E> binder) {
        bindings.put(id, binder);
    }

    public void addEq(E t) {
        String e = UUID.randomUUID().toString();
        bindings.put(e, new Binder<E>(t));
    }

    public E find(String id) {
        Env<E> curr = this;
        while (curr != null) {
            Binder<E> b = curr.bindings.get(id);
            if (b != null) return b.val;
            curr = curr.anc;
        }
        return null;
    }

    public String findBinderId(String id) {
        Env<E> curr = this;
        while (curr != null) {
            Binder<E> b = curr.bindings.get(id);
            if (b != null) return b.id;
            curr = curr.anc;
        }
        return null;
    }

    public Set<String> filterIds(Env<E> stop, Predicate<E> pred) {
        Set<String> result = new HashSet<String>();
        Env<E> curr = this;
        while (curr != stop) {
            curr.bindings.forEach((id, val) -> {
                if (pred.test(val.val)) result.add(id);
            });
            curr = curr.anc;
        }
        return result;
    }

    public ASTNode findEq(String id) {
        // TODO: review
        Env<E> curr = this;
        while (curr != null) {
            for (Binder<E> b : curr.bindings.values())
                if (b.val instanceof ASTTEq teq && teq.getTerm1() instanceof ASTId nid && id.equals(nid.getId()))
                    return teq.getTerm2();
            curr = curr.anc;
        }
        curr = this;
        while (curr != null) {
            for (Binder<E> b : curr.bindings.values())
                if (b.val instanceof ASTTEq teq && teq.getTerm2() instanceof ASTId nid && id.equals(nid.getId()))
                    return teq.getTerm1();
            curr = curr.anc;
        }
        return null;
    }

    public E findProof(ASTNode t1, ASTNode t2, Env<ASTType> sigma, Env<ASTType> phi) {
        Env<E> curr = this;
        while (curr != null) {
            for (Map.Entry<String, Binder<E>> entry : curr.bindings.entrySet())
                if (entry.getValue().val instanceof ASTTEq teq) {
                    if (teq.getTerm1() instanceof ASTId || teq.getTerm2() instanceof ASTId) continue;
                    Debug.log("Testing proof: " + entry.getValue());
                    Debug.open();
                    if ((DefEq.termdefeq(t1, teq.getTerm1(), sigma, phi, false) && DefEq.termdefeq(t2, teq.getTerm2(), sigma, phi, false))
                    || (DefEq.termdefeq(t1, teq.getTerm2(), sigma, phi, false) && DefEq.termdefeq(t2, teq.getTerm1(), sigma, phi, false)))
                        return entry.getValue().val;
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
