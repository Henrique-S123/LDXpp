package proj.types;

import proj.ast.*;
import proj.env.*;
import proj.env.EnvSet.ENV;

public class ASTTEq implements ASTType {
    ASTNode term1, term2;
    ASTType type;

    public ASTTEq(ASTNode t1, ASTNode t2, ASTType t) {
        term1 = t1;
        term2 = t2;
        type = t;
    }

    public ASTNode getTerm1() {
        return term1;
    }

    public ASTNode getTerm2() {
        return term2;
    }

    public ASTType getType() {
        return type;
    }

    public String toString() {
        return String.format("%s ≡ %s : %s", term1, term2, type);
    }

    public boolean isSubtypeOf(ASTType o, EnvSet e, AlphaEnv alpha) {
        if (o instanceof ASTTId) return isSubtypeOf(e.unfold(o), e, alpha);
        return o instanceof ASTTEq eq && type.isSubtypeOf(eq.getType(), e, alpha)
            && term1.defequals(eq.getTerm1(), e.getEnv(ENV.SIGMA), alpha)
            && term2.defequals(eq.getTerm2(), e.getEnv(ENV.SIGMA), alpha);
    }

    public boolean defequals(ASTType o, Env<ASTType> sigma, AlphaEnv alpha) {
        return o instanceof ASTTEq eq && term1.defequals(eq.getTerm1(), sigma, alpha)
            && term2.defequals(eq.getTerm2(), sigma, alpha) && type.defequals(eq.getType(), sigma, alpha);
    }

    public ASTType inst(String instId, ASTNode n) {
        ASTType instType = type.inst(instId, n);
        ASTNode instTerm1 = term1.subs(instId, n);
        ASTNode instTerm2 = term2.subs(instId, n);
        return new ASTTEq(instTerm1, instTerm2, instType);
    }
}

