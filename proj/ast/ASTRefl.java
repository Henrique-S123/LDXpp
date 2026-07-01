package proj.ast;

import proj.values.*;
import proj.types.*;
import proj.defeq.DefEq;
import proj.env.*;
import proj.errors.*;

public class ASTRefl extends ASTNode  {
    private final ASTNode term;
    private final boolean hyp;

    public ASTRefl(ASTNode t, boolean h) {
        term = t;
        hyp = h;
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
        if (DefEq.termdefeq(left.weaknorm(), right.weaknorm(), sig, e.getPhi(), hyp)) return target;
        throw new TypeCheckError(ErrorMessages.termsNotDefeq(left, right));
    }

    public ASTType puretypecheck(Env<ASTType> sigma, Env<ASTType> phi, ASTType target) throws TypeCheckError {
        if (term == null && target == null) throw new TypeCheckError(ErrorMessages.missingTermAnnotation());
        if (target == null) return new ASTTEq(term, term, term.puretypecheck(sigma, phi, null));

        if (!(target instanceof ASTTEq tt))
            throw new TypeCheckError(ErrorMessages.illegalTypeToUnary("refl", target));

        Env<ASTType> sig = (tt.getSig() != null) ? tt.getSig() : sigma;
        ASTNode left = tt.getTerm1(), right = tt.getTerm2();
        if (DefEq.termdefeq(left.weaknorm(), right.weaknorm(), sig, phi, hyp)) return target;
        throw new TypeCheckError(ErrorMessages.termsNotDefeq(left, right));
    }

    @Override
    public String toString() {
        return "refl";
    }
}
