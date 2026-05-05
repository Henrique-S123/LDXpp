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
        if (o instanceof ASTTId) {
            ASTType to = e.unfold(o);
            return this.isSubtypeOf(to, e);
        }
        return o instanceof ASTTEq eq && type.isSubtypeOf(eq.getType(), e)
            && term1.defequals(eq.getTerm1(), e.getEnv(ENV.SIGMA)) && term2.defequals(eq.getTerm2(), e.getEnv(ENV.SIGMA));
    }

    public boolean defequals(ASTType o, Environment<ASTType> sigma, Environment<ASTNode> alphaL, Environment<ASTNode> alphaR) {
        if (o instanceof ASTTEq eq) {
            ASTNode t1 = term1.normalize(sigma, alphaL);
            ASTNode t2 = term2.normalize(sigma, alphaL);
            ASTNode ot1 = eq.getTerm1().normalize(sigma, alphaR);
            ASTNode ot2 = eq.getTerm2().normalize(sigma, alphaR);
            ASTType t = eq.getType();
            return t1.defequals(ot1, sigma) && t2.defequals(ot2, sigma) && type.defequals(t, sigma, alphaL, alphaR);
        }
        return false;
    }
}

