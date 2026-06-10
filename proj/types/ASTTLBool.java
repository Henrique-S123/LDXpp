package proj.types;

import proj.env.*;

public class ASTTLBool extends ASTLinType {

    public String toString() {
        return "linbool";
    }

    public boolean isSubtypeOf(ASTType o, Env<ASTType> phi, AlphaEnv alpha) {
        if (o instanceof ASTTId) return isSubtypeOf(phi.unfold(o), phi, alpha);
        return o instanceof ASTTLBool;
    }

    public boolean defequals(Env<ASTType> sl, ASTType o, Env<ASTType> sr, AlphaEnv alpha) {
        return o instanceof ASTTLBool;
    }
}