package proj.ast;

import proj.values.*;
import proj.types.*;
import proj.env.*;
import proj.errors.ErrorMessages;
import proj.errors.TypeCheckError;

public class ASTLInt extends ASTNode  {
    int v;

    public ASTLInt(int v0) {
        v = v0;
    }

    public int getVal() {
        return v;
    }

    public IValue eval(Env<IValue> e) {
        return new VInt(v, true);
    }

    public ASTType typecheck(EnvSet e, ASTType target) {
        return new ASTTLInt();
    }

    public ASTType puretypecheck(Env<ASTType> sigma, Env<ASTType> phi, ASTType target) throws TypeCheckError {
        if (target == null || target instanceof ASTTLInt) return new ASTTLInt();
        throw new TypeCheckError(ErrorMessages.typeMismatch("linint", target));
    }

    @Override
    public String toString() {
        return String.valueOf(v) + "l";
    }
}
