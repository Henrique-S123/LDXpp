package proj.ast;

import proj.values.*;
import proj.types.*;
import proj.env.*;

public class ASTBool extends ASTNode  {
    private final boolean b;

    public ASTBool(boolean b0) {
        b = b0;
    }

    public boolean getVal() {
        return b;
    }

    public IValue eval(Env<IValue> e) {
        return new VBool(b, false);                
    }

    public ASTType typecheck(EnvSet e, ASTType target) {
        return new ASTTBool();
    }

    public ASTType puretypecheck(Env<ASTType> sigma, Env<ASTType> phi, ASTType target) {
        return new ASTTBool();
    }

    @Override
    public String toString() {
        return String.valueOf(b);
    }
}
