package proj.src.values;

import proj.src.ast.*;
import proj.src.env.*;

public class VClos implements IValue {
    Env<IValue> env;
    String id;
    ASTNode body;
    boolean lin;

    public VClos(Env<IValue> e, String i, ASTNode b, boolean l) {
        env = e;
        id = i;
        body = b;
        lin = l;
    }

    public Env<IValue> getEnv() {
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

    public String toString() {
        String res = lin ? "Linear closure with " : "Closure with ";
        res += (id != null) ? "arg " + id : "no arg";
        res += " and body {" + body + "}";
        return res;
    }
}