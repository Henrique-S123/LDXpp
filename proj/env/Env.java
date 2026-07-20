package proj.env;

import proj.ast.*;
import proj.debug.Debug;
import proj.defeq.DefEq;
import proj.types.*;

import java.util.*;

public class Env<E>{
    Env<E> anc;
    Map<String, Binder<E>> bindings;
    ASTTEq lastUsedEq;

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

    public Map<String, Binder<E>> getBindings() {
        return bindings;
    }

    public Env<E> copy() {
        Env<E> e = new Env<>((this.anc == null ? null : this.anc.copy()));
        e.bindings = new HashMap<>(bindings);
        return e;
    }

    public void assoc(String id, E val) {
        // TODO: add guard
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

    public ASTNode findEq(String id) {
        // TODO: optimize
        Env<E> curr = this;
        while (curr != null) {
            for (Binder<E> b : curr.bindings.values())
                if (b.val instanceof ASTTEq teq && teq.getTerm1() instanceof ASTId nid && id.equals(nid.getId())) {
                    if (teq.getTerm1() instanceof ASTId && teq.getTerm2() instanceof ASTId) {
                        if (teq != lastUsedEq) lastUsedEq = teq;
                        else continue;
                    }
                    return teq.getTerm2();
                }
            curr = curr.anc;
        }
        curr = this;
        while (curr != null) {
            for (Binder<E> b : curr.bindings.values())
                if (b.val instanceof ASTTEq teq && teq.getTerm2() instanceof ASTId nid && id.equals(nid.getId())) {
                    if (teq.getTerm1() instanceof ASTId && teq.getTerm2() instanceof ASTId) {
                        if (teq != lastUsedEq) lastUsedEq = teq;
                        else continue;
                    }
                    return teq.getTerm1();
                }
            curr = curr.anc;
        }
        return null;
    }

    public E findProof(Env<ASTType> sigma, ASTNode t1, ASTNode t2, AlphaEnv alpha, Env<ASTType> phi) {
        Env<E> curr = this;
        while (curr != null) {
            for (Map.Entry<String, Binder<E>> entry : curr.bindings.entrySet())
                if (entry.getValue().val instanceof ASTTEq teq) {
                    // TODO: try to use this optimization
                    // if (teq.getTerm1() instanceof ASTId || teq.getTerm2() instanceof ASTId) continue;
                    Debug.log("Testing proof: " + entry.getValue());
                    Debug.open();
                    E res = null;
                    DefEq e = new DefEq(sigma);
                    if ((e.termdefeq(t1, teq.getTerm1(), sigma, phi, alpha) && e.termdefeq(t2, teq.getTerm2(), sigma, phi, alpha))
                    || (e.termdefeq(t1, teq.getTerm2(), sigma, phi, alpha) && e.termdefeq(t2, teq.getTerm1(), sigma, phi, alpha)))
                        res = entry.getValue().val;
                    Debug.close();
                    Debug.nl();
                    if (res != null) return res;
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
