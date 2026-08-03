package proj.src.ast;

import proj.src.values.*;
import proj.src.types.*;
import proj.src.env.*;

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

    public ASTType puretypecheck(PureEnvSet pe, ASTType target) {
        return new ASTTBool(lin);
    }

    @Override
    public String toString() {
        return String.valueOf(b) + (lin ? "l" : "");
    }
}
