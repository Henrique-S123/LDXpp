package proj.ast;

import proj.values.*;
import proj.types.*;
import proj.env.*;
import proj.errors.ErrorMessages;
import proj.errors.TypeCheckError;

public class ASTBool extends ASTNode  {
    boolean b;

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

    public ASTType puretypecheck(Env<ASTType> sigma, Env<ASTType> phi, ASTType target) throws TypeCheckError {
        if (target == null || target instanceof ASTTBool) return new ASTTBool();
        throw new TypeCheckError(ErrorMessages.typeMismatch("bool", target));
    }

    @Override
    public String toString() {
        return String.valueOf(b);
    }
}
