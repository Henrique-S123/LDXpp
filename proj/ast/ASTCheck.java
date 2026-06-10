package proj.ast;

import proj.values.*;
import proj.types.*;
import proj.defeq.DefEq;
import proj.env.*;
import proj.errors.*;

public class ASTCheck extends ASTNode {
    ASTNode left, right;

    public ASTCheck(ASTNode l, ASTNode r) {
        left = l;
        right = r;
    }
    
    public IValue eval(Env<IValue> e) {
        return new VBool(true, false);
    }

    public ASTType typeinfer(EnvSet e) throws TypeCheckError, EnvironmentError {
        ASTType t = left.typeinfer(e);
        ASTType t2 = right.typeinfer(e);
        if (!t.isSubtypeOf(t2, e, new AlphaEnv()) || !t2.isSubtypeOf(t, e, new AlphaEnv()))
            throw new TypeCheckError(ErrorMessages.termsWithDifferentTypes(left, right, t, t2));

        Env<ASTType> sigma = e.getSigma();
        if (DefEq.termdefeq(left.weaknorm(), sigma, right.weaknorm(), sigma, e.getPhi())) return new ASTTEq(left, right, t);
        throw new TypeCheckError(ErrorMessages.termsNotDefeq(left, right));
    }

    @Override
    public String toString() {
        return String.format("%s ? %s", left, right);
    }
}
