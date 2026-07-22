package proj.src.ast;

import proj.src.values.*;
import proj.src.types.*;
import proj.src.defeq.DefEq;
import proj.src.env.*;
import proj.src.errors.*;

public class ASTCheckTypes extends ASTNode {
    private final ASTType left, right;

    public ASTCheckTypes(ASTType l, ASTType r) {
        left = l;
        right = r;
    }
    
    public IValue eval(Env<IValue> e) {
        return new VBool(true, false);
    }

    public ASTType typecheck(EnvSet e, ASTType target) throws TypeCheckError {
        DefEq eq = new DefEq(e.getSigma());
        if (eq.typedefeq(left, right, e.getSigma(), e.getPhi())) return new ASTTUnit();
        throw new TypeCheckError(ErrorMessages.typesNotDefeq(left, right));
    }

    @Override
    public String toString() {
        return String.format("%s ?T %s", left, right);
    }
}
