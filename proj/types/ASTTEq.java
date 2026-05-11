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

    public boolean isSubtypeOf(ASTType o, EnvSet e) {
        if (o instanceof ASTTId) return isSubtypeOf(e.unfold(o), e);
        return o instanceof ASTTEq eq && type.isSubtypeOf(eq.getType(), e)
            && term1.defequals(eq.getTerm1(), e.getEnv(ENV.SIGMA), new AlphaEnv())
            && term2.defequals(eq.getTerm2(), e.getEnv(ENV.SIGMA), new AlphaEnv());
    }

    public boolean defequals(ASTType o, Env<ASTType> sigma, AlphaEnv alpha) {
        return o instanceof ASTTEq eq && term1.defequals(eq.getTerm1(), sigma, alpha)
            && term2.defequals(eq.getTerm2(), sigma, alpha) && type.defequals(eq.getType(), sigma, alpha);
    }

    public ASTType inst(String instId, ASTNode n) {
        Env<ASTNode> e = new Env<ASTNode>();
        e.assoc(instId, n);
        ASTType instType = type.inst(instId, n);
        ASTNode instTerm1 = (term1 instanceof ASTId nid && nid.getId().equals(instId)) ? n : term1;
        ASTNode instTerm2 = (term2 instanceof ASTId nid && nid.getId().equals(instId)) ? n : term2;
        // FIXME: add inst method to terms
        // ASTNode instTerm1 = term1.inst(instId, n);
        // ASTNode instTerm2 = term2.inst(instId, n);
        return new ASTTEq(instTerm1, instTerm2, instType);
    }
}

