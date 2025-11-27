package proj.env;

import proj.types.*;
import proj.errors.*;

import java.util.*;

public class Environment <E>{
    Environment<E> anc;
    Map<String, E> bindings;

    public Environment(){
        anc = null;
        bindings = new HashMap<String,E>();
    }
    
    public Environment(Environment<E> ancestor){
        anc = ancestor;
        bindings = new HashMap<String,E>();
    }

    private void setBindings(Map<String, E> m) {
        this.bindings = m;
    }

    public Environment<E> beginScope(){
        return new Environment<E>(this);
    }
    
    public Environment<E> endScope(){
        return anc;
    }

    public boolean isEmpty() {
        return bindings.isEmpty();
    }

    public Environment<E> copy(boolean deep) {
        Environment<E> e = new Environment<>(deep ? (this.anc == null ? null : this.anc.copy(deep)) : this.anc);
        e.setBindings(new HashMap<String,E>(this.bindings));
        return e;
    }

    public void assoc(String id, E bind) {
        bindings.put(id, bind);
    }

    public E find(String id, boolean consume) throws InterpreterError {
        Environment<E> curr = this;
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

    public String toStr() {
        String res = "[";

        for (String s : bindings.keySet()) {
            E val = bindings.get(s);
            String valStr = (val instanceof ASTType ? ((ASTType) val).toStr() : val.toString());
            res += s + ": " + valStr + "; ";
        }

        if (bindings.size() > 0) {
            res = res.substring(0, res.length()-2);
        }

        return res += "]";
    }

    public ASTType unfold(ASTType t) throws InterpreterError {
        if (t instanceof ASTTId) {
            return unfold((ASTType) find(((ASTTId) t).getId(), false));
        } else {
            return t;
        }
    }
}
