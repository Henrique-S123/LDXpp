package proj.values;

import proj.ast.*;
import proj.env.*;

public class VRec implements IValue {
    Env<IValue> env;
    String fid;
    ASTNode body;
    boolean lin;

    public VRec(Env<IValue> e, String i, ASTNode b, boolean l) {
        env = e;
        fid = i;
        body = b;
        lin = l;
    }

    public Env<IValue> getEnv() {
        return env;
    }

    public String getFid() {
        return fid;
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
        return String.format("%s closure with function id %s and body %s",
            lin ? "Linear recursive" : "Recursive", fid, body);
    }
}