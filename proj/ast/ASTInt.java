package proj.ast;

import proj.values.*;
import proj.types.*;
import proj.env.*;
import proj.errors.ErrorMessages;
import proj.errors.TypeCheckError;

public class ASTInt extends ASTNode  {
    int v;

    public ASTInt(int v0) {
        v = v0;
    }

    public int getVal() {
        return v;
    }

    public IValue eval(Env<IValue> e) {
        return new VInt(v, false);                
    }

    public ASTType typecheck(EnvSet e, ASTType target) {
        return new ASTTInt();
    }

    public ASTType puretypecheck(Env<ASTType> sigma, Env<ASTType> phi, ASTType target) throws TypeCheckError {
        if (target == null || target instanceof ASTTInt) return new ASTTInt();
        throw new TypeCheckError(ErrorMessages.typeMismatch("int", target));
    }

    @Override
    public String toString() {
        return String.valueOf(v);
    }
}
