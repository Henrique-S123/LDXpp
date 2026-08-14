package proj.src.ast;

import proj.src.values.*;
import proj.src.types.*;
import proj.src.defeq.*;
import proj.src.env.*;
import proj.src.errors.*;

public class ASTRefl extends ASTNode  {
    private final ASTNode term;
    private final Tactic tactic;

    public ASTRefl(ASTNode t, Tactic tac) {
        term = t; tactic = tac;
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

        ASTNode left = tt.getTerm1(), right = tt.getTerm2();
        DefEq eq = new DefEq(e.getSigma());
        if (eq.termdefeq(left.weaknorm(), right.weaknorm(), e.getPhi(), e.getAlpha(), tactic)) return target;
        throw new TypeCheckError(ErrorMessages.termsNotDefeq(left, right));
    }

    public ASTType puretypecheck(PureEnvSet pe, ASTType target) throws TypeCheckError {
        if (target == null) {
            if (term == null) throw new TypeCheckError(ErrorMessages.missingTermAnnotation());
            return new ASTTEq(term, term, term.puretypecheck(pe, null));
        }

        if (!(target instanceof ASTTEq tt))
            throw new TypeCheckError(ErrorMessages.illegalTypeToUnary("refl", target));

        ASTNode left = tt.getTerm1(), right = tt.getTerm2();
        DefEq eq = new DefEq(pe.getSigma());
        if (eq.termdefeq(left.weaknorm(), right.weaknorm(), pe.getPhi(), pe.getAlpha(), tactic)) return target;
        throw new TypeCheckError(ErrorMessages.termsNotDefeq(left, right));
    }

    @Override
    public String toString() {
        return String.format("refl%s", term == null ? "" : "(" + term + ")");
    }
}
