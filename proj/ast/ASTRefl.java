package proj.ast;

import proj.values.*;
import proj.types.*;
import proj.defeq.*;
import proj.env.*;
import proj.errors.*;

public class ASTRefl extends ASTNode  {
    private final ASTNode term;
    private final Tactic tactic;

    public ASTRefl(ASTNode t, Tactic tac) {
        term = t;
        tactic = tac;
    }

    public IValue eval(Env<IValue> e) {
        return new VRefl();
    }

    public ASTType typecheck(EnvSet e, ASTType target) throws TypeCheckError {
        if (target == null) {
            if (term == null) throw new TypeCheckError(ErrorMessages.missingTermAnnotation());
            return new ASTTEq(term, term, term.typecheck(e, null));
        }

        if (!(target instanceof ASTTEq tt))
            throw new TypeCheckError(ErrorMessages.illegalTypeToUnary("refl", target));

        Env<ASTType> sig = (tt.getSig() != null) ? tt.getSig() : e.getSigma();
        ASTNode left = tt.getTerm1(), right = tt.getTerm2();
        DefEq eq = new DefEq(e.getSigma());
        if (eq.termdefeq(left.weaknorm(), right.weaknorm(), sig, e.getPhi(), e.getAlpha(), tactic)) return target;
        throw new TypeCheckError(ErrorMessages.termsNotDefeq(left, right));
    }

    public ASTType puretypecheck(Env<ASTType> sigma, Env<ASTType> phi, AlphaEnv alpha, ASTType target) throws TypeCheckError {
        if (target == null) {
            if (term == null) throw new TypeCheckError(ErrorMessages.missingTermAnnotation());
            return new ASTTEq(term, term, term.puretypecheck(sigma, phi, alpha, null));
        }

        if (!(target instanceof ASTTEq tt))
            throw new TypeCheckError(ErrorMessages.illegalTypeToUnary("refl", target));

        Env<ASTType> sig = (tt.getSig() != null) ? tt.getSig() : sigma;
        ASTNode left = tt.getTerm1(), right = tt.getTerm2();
        DefEq eq = new DefEq(sigma);
        if (eq.termdefeq(left.weaknorm(), right.weaknorm(), sig, phi, alpha, tactic)) return target;
        throw new TypeCheckError(ErrorMessages.termsNotDefeq(left, right));
    }

    @Override
    public String toString() {
        return "refl";
    }
}
