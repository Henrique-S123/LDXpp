package proj.ast;

import proj.values.*;
import proj.types.*;
import proj.env.*;

public class ASTInt extends ASTNode  {
    private final int v;
    private final boolean lin;

    public ASTInt(int v0, boolean l) {
        v = v0; lin = l;
    }

    public int getVal() { return v; }

    public boolean isLinear() { return lin; }

    public IValue eval(Env<IValue> e) {
        return new VInt(v, lin);                
    }

    public ASTType typecheck(EnvSet e, ASTType target) {
        return new ASTTInt(lin);
    }

    public ASTType puretypecheck(Env<ASTType> sigma, Env<ASTType> phi, AlphaEnv alpha, ASTType target) {
        return new ASTTInt(lin);
    }

    @Override
    public String toString() {
        return String.valueOf(v) + (lin ? "l" : "");
    }
}
