package proj.src.types;

import proj.src.ast.*;
import proj.src.debug.Debug;
import proj.src.env.*;
import proj.src.errors.*;
import proj.src.defeq.*;

public class ASTTEq extends ASTType {
    private final ASTNode term1, term2;
    private final ASTType type;

    public ASTTEq(ASTNode t1, ASTNode t2, ASTType t) {
        term1 = t1; term2 = t2; type = t; lin = false;
    }

    public ASTNode getTerm1() { return term1; }

    public ASTNode getTerm2() { return term2; }

    public ASTType getType() { return type; }

    public String toString() {
        return String.format("%s ≡ %s : %s", term1, term2, type);
    }

    public boolean isSubtypeOf(ASTType o, PureEnvSet pe, AlphaEnv alpha) {
        if (o instanceof ASTTId) return isSubtypeOf(pe.unfold(o), pe, alpha);
        Debug.off();
        DefEq e = new DefEq(pe.getSigma());
        boolean res = o instanceof ASTTEq eq && e.typedefeq(type, eq.getType(), pe.getPhi(), alpha)
            && e.termdefeq(term1.weaknorm(), eq.getTerm1().weaknorm(), pe.getPhi(), alpha)
            && e.termdefeq(term2.weaknorm(), eq.getTerm2().weaknorm(), pe.getPhi(), alpha);
        Debug.on();
        return res;
    }

    public ASTTEq inst(String instId, ASTNode n) {
        return new ASTTEq(term1.subs(instId, n), term2.subs(instId, n), type.inst(instId, n));
    }

    public ASTType check(PureEnvSet pe) throws TypeCheckError {
        type.check(pe);
        ASTType type1 = term1.puretypecheck(pe, type);
        if (!type1.isSubtypeOf(type, pe))
            throw new TypeCheckError(ErrorMessages.notSubtype(type1, type));
        ASTType type2 = term2.puretypecheck(pe, type);
        if (!type2.isSubtypeOf(type, pe))
            throw new TypeCheckError(ErrorMessages.notSubtype(type2, type));
        return this;
    }
}

