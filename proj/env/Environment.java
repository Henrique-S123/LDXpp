package proj.env;

import proj.types.*;
import proj.errors.*;
import proj.values.*;

import java.util.*;

public class Environment <E>{
    Environment<E> anc;
    Map<String, E> gamma;
    Map<String, E> delta;
    List<String> usedLinears;

    public Environment(){
        anc = null;
        gamma = new HashMap<String,E>();
        delta = new HashMap<String,E>();
        usedLinears = new ArrayList<String>();
    }
    
    public Environment(Environment<E> ancestor){
        anc = ancestor;
        gamma = new HashMap<String,E>();
        delta = new HashMap<String,E>();
        usedLinears = new ArrayList<String>();
    }

    private void setGamma(Map<String, E> g) {
        this.gamma = g;
    }

    private void setDelta(Map<String, E> g) {
        this.delta = g;
    }

    private void setUsedLinears(List<String> l) {
        this.usedLinears = l;
    }

    public Environment<E> beginScope(){
        return new Environment<E>(this);
    }
    
    public Environment<E> endScope(){
        return anc;
    }

    public Environment<E> copy() {
        Environment<E> e = new Environment<>(this.anc);
        e.setGamma(new HashMap<String,E>(this.gamma));
        e.setDelta(new HashMap<String,E>(this.delta));
        e.setUsedLinears(new ArrayList<String>(this.usedLinears));
        return e;
    }

    public void assoc(String id, E bind) throws InterpreterError {
        if (gamma.containsKey(id) || delta.containsKey(id)) throw new InterpreterError("Identifier " + id + " already declared!");
        if (bind instanceof ASTLinType) {
            delta.put(id, bind);
            if (usedLinears.contains(id))
                usedLinears.remove(id);
        } else {
            gamma.put(id, bind);
        }
    }

    public void assocUnrestricted(String id, E bind) throws InterpreterError {
        if (gamma.containsKey(id)) throw new InterpreterError("Identifier " + id + " already declared!");
        gamma.put(id, bind);
    }

    public E find(String id) throws InterpreterError {
        Environment<E> curr = this;
        while (curr != null) {
            E val = curr.gamma.get(id);
            if (val != null) {
                return val;
            }
            val = curr.delta.get(id);
            if (val != null) {
                curr.delta.remove(id);
                usedLinears.add(id);
                return val;
            }
            curr = curr.anc;
        }
        if (usedLinears.contains(id))
            throw new InterpreterError("Linear value of '" + id + "' has already been consumed and cannot be used again.");
        else
            throw new InterpreterError("Undeclared identifier " + id + ".");
    }

    public Set<String> getLinears() {
        return delta.keySet();
    }

    public List<String> getUsedLinears() {
        return usedLinears;
    }

    public String toStr() {
        String res = "Gamma: [";

        for (String s : gamma.keySet()) {
            res += s + ": " + gamma.get(s) + "; ";
        }

        if (gamma.size() > 0) {
            res = res.substring(0, res.length()-2);
        }

        res += "], Delta: [";

        for (String s : delta.keySet()) {
            res += s + ": " + delta.get(s) + "; ";
        }

        if (delta.size() > 0) {
            res = res.substring(0, res.length()-2);
        }

        return res + "]";
    }

    public String deltaToStr() {
        String res = "[";

        for (String s : delta.keySet()) {
            res += s + ": ";
            E val = delta.get(s);
            if (val instanceof IValue) {
                res += ((IValue) val).toStr();
            } else if (val instanceof ASTType) {
                res += ((ASTType) val).toStr();
            } else {
                res += delta.get(s);
            }
            res += "; ";
        }

        if (delta.size() > 0) {
            res = res.substring(0, res.length()-2);
        }

        return res + "]";
    }

    public ASTType unfold(ASTType t) throws InterpreterError {
        if (t instanceof ASTTId) {
            return unfold((ASTType) find(((ASTTId) t).getId()));
        } else {
            return t;
        }
    }
}
