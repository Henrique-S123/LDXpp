package proj.values;

import proj.ast.*;
import proj.env.*;

public class VClos implements IValue {
    Environment<IValue> env;
    String id;
    ASTNode body;
    boolean lin;

    public VClos(Environment<IValue> e, String i, ASTNode b, boolean l) {
        env = e;
        id = i;
        body = b;
        lin = l;
    }

    public Environment<IValue> getEnv() {
        return env;
    }

    public String getId() {
        return id;
    }

    public ASTNode getBody() {
        return body;
    }

    public void setBody(ASTNode b) {
        body = b;
    }

    public boolean islin() {
        return lin;
    }

    public String toStr() {
        String res = lin ? "Linear closure with " : "Closure with ";
        res += (id != null) ? "arg " + id : "no arg";
        res += " and environment " + env.toStr();
        return res;
    }
}