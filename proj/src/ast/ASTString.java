package proj.src.ast;

import proj.src.values.*;
import proj.src.types.*;
import proj.src.env.*;

public class ASTString extends ASTNode  {
    private final String val;

    public ASTString(String s) {
        val = s;
    }

    public String getVal() { return val; }

    public IValue eval(Env<IValue> e) {
        return new VString(val);                
    }

    public ASTType typecheck(EnvSet e, ASTType target) {
        return new ASTTString();
    }

    public ASTType puretypecheck(PureEnvSet pe, ASTType target) {
        return new ASTTString();
    }

    @Override
    public String toString() {
        return val;
    }
}
