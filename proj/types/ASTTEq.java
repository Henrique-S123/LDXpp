package proj.types;

import proj.ast.*;
import proj.defeq.DefEq;
import proj.env.*;

public class ASTTEq extends ASTType {
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
        Env<ASTType> sigma = e.getSigma();
        return o instanceof ASTTEq eq && type.isSubtypeOf(eq.getType(), e, alpha)
            && DefEq.termdefeq(term1, sigma, eq.getTerm1(), sigma, alpha)
            && DefEq.termdefeq(term2, sigma, eq.getTerm2(), sigma, alpha);
    }

    public ASTType inst(String instId, ASTNode n) {
        ASTType instType = type.inst(instId, n);
        ASTNode instTerm1 = term1.subs(instId, n);
        ASTNode instTerm2 = term2.subs(instId, n);
        return new ASTTEq(instTerm1, instTerm2, instType);
    }
}

