package proj.ast;

import proj.values.*;
import proj.types.*;
import proj.env.*;
import proj.errors.ErrorMessages;
import proj.errors.TypeCheckError;

public class ASTString extends ASTNode  {
    String s;

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

    public ASTType puretypecheck(Env<ASTType> sigma, Env<ASTType> phi, ASTType target) throws TypeCheckError {
        if (target == null || target instanceof ASTTString) return new ASTTString();
        throw new TypeCheckError(ErrorMessages.typeMismatch("string", target));
    }

    @Override
    public String toString() {
        return s;
    }
}
