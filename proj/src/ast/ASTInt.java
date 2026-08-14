package proj.src.ast;

import proj.src.values.*;
import proj.src.types.*;
import proj.src.env.*;

public class ASTInt extends ASTNode  {
    private final int val;
    private final boolean lin;

    public ASTInt(int v, boolean l) {
        val = v; lin = l;
    }

    public int getVal() { return val; }

    public boolean isLinear() { return lin; }

    public IValue eval(Env<IValue> e) {
        return new VInt(val, lin);                
    }

    public ASTType typecheck(EnvSet e, ASTType target) {
        return new ASTTInt(lin);
    }

    public ASTType puretypecheck(PureEnvSet pe, ASTType target) {
        return new ASTTInt(lin);
    }

    @Override
    public String toString() {
        return String.valueOf(val) + (lin ? "l" : "");
    }
}
