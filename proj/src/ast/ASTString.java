package proj.src.ast;

import proj.src.values.*;
import proj.src.types.*;
import proj.src.env.*;

public class ASTString extends ASTNode  {
    private final String s;

    public ASTString(String s0) {
        s = s0;
    }
    public String getVal() {
      return s;
    }

    public IValue eval(Env<IValue> e) {
        return new VString(s);                
    }

    public ASTType typecheck(EnvSet e, ASTType target) {
        return new ASTTString();
    }

    public ASTType puretypecheck(Env<ASTType> sigma, Env<ASTType> phi, AlphaEnv alpha, ASTType target) {
        return new ASTTString();
    }

    @Override
    public String toString() {
        return s;
    }
}
