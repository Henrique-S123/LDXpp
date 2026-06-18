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

    public ASTType typecheck(EnvSet e, ASTType target) throws TypeCheckError {
        ASTType t = left.typecheck(e, null);
        ASTType t2 = right.typecheck(e, null);
        if (!t.isSubtypeOf(t2, e.getSigma(), e.getPhi(), new AlphaEnv()) || !t2.isSubtypeOf(t, e.getSigma(), e.getPhi(), new AlphaEnv()))
            throw new TypeCheckError(ErrorMessages.termsWithDifferentTypes(left, right, t, t2));

        Env<ASTType> sigma = e.getSigma();
        ASTNode ln = left.weaknorm();
        ASTNode rn = right.weaknorm();
        if (DefEq.termdefeq(ln, sigma, rn, sigma, e.getPhi())) return new ASTTEq(left, right, t);
        throw new TypeCheckError(ErrorMessages.termsNotDefeq(left, right));
    }

    @Override
    public String toString() {
        return String.format("%s ? %s", left, right);
    }
}
