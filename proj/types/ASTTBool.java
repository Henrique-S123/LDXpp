package proj.types;

import proj.env.*;

public class ASTTBool implements ASTType {

    public String toString() {
        return "bool";
    }

    public boolean isSubtypeOf(ASTType o, EnvSet e) {
        if (o instanceof ASTTId) return isSubtypeOf(e.unfold(o), e);
        return o instanceof ASTTBool || o instanceof ASTTLBool;
    }

    public boolean defequals(ASTType o, Env<ASTType> sigma, AlphaEnv alpha) {
        return o instanceof ASTTBool;
    }
}