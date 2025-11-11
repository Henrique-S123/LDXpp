package proj.values;

import proj.ast.*;
import proj.env.*;

public class VLcons implements IValue {
    ASTNode f, s;
    Environment<IValue> e;
    IValue vf = null, vs = null;

    public VLcons(ASTNode f0, ASTNode s0, Environment<IValue> e0) {
        f = f0;
        s = s0;
        e = e0;
    }

    public ASTNode getFirst() { return f; }

    public ASTNode getSecond() { return s; }

    public IValue getFirstValue() { return vf; }

    public IValue getSecondValue() { return vs; }

    public Environment<IValue> getEnv() { return e; }

    public void setFirst(IValue vf0) { vf = vf0; }

    public void setSecond(IValue vs0) { vs = vs0; }

    public String toStr() {
        if (vf != null && vs != null) {
            return "(" + vf.toStr() + ":?" + vs.toStr() + ")";
        } else {
            return "(" + f + ":?" + s + ")";
        }
    }
}