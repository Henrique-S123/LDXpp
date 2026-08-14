package proj.src.ast;

import proj.src.values.*;
import proj.src.types.*;
import proj.src.env.*;

public class ASTBool extends ASTNode  {
    private final boolean val;
    private final boolean lin;

    public ASTBool(boolean b, boolean l) {
        val = b; lin = l;
    }

    public boolean getVal() { return val; }

    public boolean isLinear() { return lin; }

    public IValue eval(Env<IValue> e) {
        return new VBool(val, lin);
    }

    public ASTType typecheck(EnvSet e, ASTType target) {
        return new ASTTBool(lin);
    }

    public ASTType puretypecheck(PureEnvSet pe, ASTType target) {
        return new ASTTBool(lin);
    }

    @Override
    public String toString() {
        return String.valueOf(val) + (lin ? "l" : "");
    }
}
