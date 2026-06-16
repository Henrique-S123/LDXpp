package proj.ast;

import proj.values.*;
import proj.types.*;
import proj.defeq.DefEq;
import proj.env.*;
import proj.errors.*;

public class ASTRefl extends ASTNode  {
    ASTNode term;

    public ASTRefl(ASTNode t) {
        term = t;
    }

    public IValue eval(Env<IValue> e) {
        return new VRefl();
    }

    public ASTType typecheck(EnvSet e, ASTType target) throws TypeCheckError {
        if (term == null && target == null) throw new TypeCheckError(ErrorMessages.missingTermAnnotation());
        if (target == null) return new ASTTEq(term, term, term.typecheck(e, null));

        if (!(target instanceof ASTTEq tt))
            throw new TypeCheckError(ErrorMessages.illegalTypeToUnary("refl", target));

        Env<ASTType> sig = (tt.getSig() != null) ? tt.getSig() : e.getSigma();
        ASTNode left = tt.getTerm1(), right = tt.getTerm2();
        if (DefEq.termdefeq(left.weaknorm(), sig, right.weaknorm(), sig, e.getPhi())) return target;
        throw new TypeCheckError(ErrorMessages.termsNotDefeq(left, right));
    }

    @Override
    public String toString() {
        return "refl";
    }
}
