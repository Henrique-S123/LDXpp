package proj.src.values;

import proj.src.ast.*;
import proj.src.env.*;

public class VRec implements IValue {
    Env<IValue> env;
    String fid;
    ASTNode body;

    public VRec(Env<IValue> e, String i, ASTNode b) {
        env = e;
        fid = i;
        body = b;
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

    public String toString() {
        return String.format("Recursive closure with function id %s and body %s", fid, body);
    }
}