package proj.types;

import proj.env.*;

public class ASTTBool extends ASTType {

    public ASTTBool() {
        lin = false;
    }

    public String toString() {
        return "bool";
    }

    public boolean isSubtypeOf(ASTType o, Env<ASTType> sigma, Env<ASTType> phi, AlphaEnv alpha) {
        if (o instanceof ASTTId) return isSubtypeOf(phi.unfold(o), sigma, phi, alpha);
        return o instanceof ASTTBool || o instanceof ASTTLBool;
    }

    public boolean defequals(Env<ASTType> sl, ASTType o, Env<ASTType> sr, AlphaEnv alpha) {
        return o instanceof ASTTBool;
    }
}