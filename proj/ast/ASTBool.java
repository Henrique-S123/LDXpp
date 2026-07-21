package proj.ast;

import proj.values.*;
import proj.types.*;
import proj.env.*;

public class ASTBool extends ASTNode  {
    private final boolean b;
    private final boolean lin;

    public ASTBool(boolean b0, boolean l) {
        b = b0; lin = l;
    }

    public boolean getVal() { return b; }

    public boolean isLinear() { return lin; }

    public IValue eval(Env<IValue> e) {
        return new VBool(b, lin);
    }

    public ASTType typecheck(EnvSet e, ASTType target) {
        return new ASTTBool(lin);
    }

    public ASTType puretypecheck(Env<ASTType> sigma, Env<ASTType> phi, AlphaEnv alpha, ASTType target) {
        return new ASTTBool(lin);
    }

    @Override
    public String toString() {
        return String.valueOf(b) + (lin ? "l" : "");
    }
}
