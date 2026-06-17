package proj.ast;

import proj.values.*;
import proj.types.*;
import proj.env.*;
import proj.errors.ErrorMessages;
import proj.errors.TypeCheckError;

public class ASTLBool extends ASTNode  {
    boolean b;

    public ASTLBool(boolean b0) {
        b = b0;
    }

    public boolean getVal() {
      return this.b;
    }

    public IValue eval(Env<IValue> e) {
        return new VBool(b, true);                
    }

    public ASTType typecheck(EnvSet e, ASTType target) {
        return new ASTTLBool();
    }

    public ASTType puretypecheck(Env<ASTType> sigma, Env<ASTType> phi, ASTType target) throws TypeCheckError {
        if (target == null || target instanceof ASTTLBool) return new ASTTLBool();
        throw new TypeCheckError(ErrorMessages.typeMismatch("linbool", target));
    }

    @Override
    public String toString() {
        return String.valueOf(b) + "l";
    }
}
