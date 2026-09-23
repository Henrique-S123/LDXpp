package proj.src.ast;

import proj.src.values.*;
import proj.src.types.*;
import proj.src.defeq.*;
import proj.src.env.*;
import proj.src.errors.*;

public class ASTRefl extends ASTNode  {
    private final ASTNode term1, term2;
    private final Tactic tactic;

    public ASTRefl(ASTNode t1, ASTNode t2, Tactic tac) {
        term1 = t1; term2 = t2; tactic = tac;
    }

    public IValue eval(Env<IValue> e) {
        return new VRefl();
    }

    public ASTType typecheck(EnvSet e, ASTType target) throws TypeCheckError {
        ASTNode left, right;
        ASTType ret, termtype;
        if (target == null) {
            if (term1 == null || term2 == null) throw new TypeCheckError(ErrorMessages.missingReflTerms());
            termtype = term1.puretypecheck(new PureEnvSet(e), null);
            term2.puretypecheck(new PureEnvSet(e), null);
            left = term1; right = term2;
            ret = new ASTTEq(left, right, termtype);
        }
        else if (target instanceof ASTTEq tt) {
            left = tt.getTerm1(); right = tt.getTerm2();
            ret = target;
        }
        else throw new TypeCheckError(ErrorMessages.illegalTypeToUnary("refl", target));

        if (DefEq.termdefeq(left.weaknorm(), right.weaknorm(), new PureEnvSet(e), new AlphaEnv(), tactic)) return ret;
        throw new TypeCheckError(ErrorMessages.termsNotDefeq(left, right));
    }

    public ASTType puretypecheck(PureEnvSet pe, ASTType target) throws TypeCheckError {
        ASTNode left, right;
        ASTType ret, termtype;
        if (target == null) {
            if (term1 == null || term2 == null) throw new TypeCheckError(ErrorMessages.missingReflTerms());
            termtype = term1.puretypecheck(pe, null);
            term2.puretypecheck(pe, null);
            left = term1; right = term2;
            ret = new ASTTEq(left, right, termtype);
        }
        else if (target instanceof ASTTEq tt) {
            left = tt.getTerm1(); right = tt.getTerm2();
            ret = target;
        }
        else throw new TypeCheckError(ErrorMessages.illegalTypeToUnary("refl", target));
        
        if (DefEq.termdefeq(left.weaknorm(), right.weaknorm(), pe, new AlphaEnv(), tactic)) return ret;
        throw new TypeCheckError(ErrorMessages.termsNotDefeq(left, right));
    }

    public boolean structEq(ASTNode o) {
        return o instanceof ASTRefl;
    }

    @Override
    public String toString() {
        return String.format("refl");
    }
}
