package proj.types;

import proj.ast.*;
import proj.debug.Debug;
import proj.env.*;
import proj.errors.*;
import proj.defeq.*;

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

    public void setSig(Env<ASTType> s) {
        sig = s;
    }

    public String toString() {
        return String.format("%s ≡ %s : %s", term1, term2, type);
    }

    public boolean isSubtypeOf(ASTType o, Env<ASTType> sigma, Env<ASTType> phi, AlphaEnv alpha) {
        if (o instanceof ASTTId) return isSubtypeOf(phi.unfold(o), sigma, phi, alpha);
        Debug.off();
        DefEq e = new DefEq(sigma);
        boolean res = o instanceof ASTTEq eq && e.typedefeq(type, eq.getType(), sigma, phi, alpha)
            && e.termdefeq(term1.weaknorm(), eq.getTerm1().weaknorm(), sigma, phi, alpha)
            && e.termdefeq(term2.weaknorm(), eq.getTerm2().weaknorm(), sigma, phi, alpha);
        Debug.on();
        return res;
    }

    public ASTType inst(String instId, ASTNode n) {
        ASTType instType = type.inst(instId, n);
        ASTNode instTerm1 = term1.subs(instId, n);
        ASTNode instTerm2 = term2.subs(instId, n);
        ASTType neweq = new ASTTEq(instTerm1, instTerm2, instType);
        neweq.setSig(sig);
        return neweq;
    }

    public ASTType check(Env<ASTType> sigma, Env<ASTType> phi, AlphaEnv alpha) throws TypeCheckError {
        type.check(sigma, phi, alpha);
        ASTType type1 = term1.puretypecheck(sigma, phi, alpha, type);
        Debug.log("LEFT TERM TYPE: " + type1);
        if (!type1.isSubtypeOf(type, sigma, phi, alpha))
            throw new TypeCheckError(ErrorMessages.notSubtype(type1, type));
        ASTType type2 = term2.puretypecheck(sigma, phi, alpha, type);
        Debug.log("RIGHT TERM TYPE: " + type2);
        if (!type2.isSubtypeOf(type, sigma, phi, alpha))
            throw new TypeCheckError(ErrorMessages.notSubtype(type2, type));
        return this;
    }
}

