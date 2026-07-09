package proj.ast;

import proj.values.*;
import proj.types.*;
import proj.env.*;

public class ASTLInt extends ASTNode  {
    private final int v;

    public ASTLInt(int v0) {
        v = v0;
    }

    public int getVal() {
        return v;
    }

    public IValue eval(Env<IValue> e) {
        return new VInt(v, true);
    }

    public ASTType typecheck(EnvSet e, ASTType target) {
        return new ASTTLInt();
    }

    public ASTType puretypecheck(Env<ASTType> sigma, Env<ASTType> phi, AlphaEnv alpha, ASTType target) {
        return new ASTTLInt();
    }

    @Override
    public String toString() {
        return String.valueOf(v) + "l";
    }
}
