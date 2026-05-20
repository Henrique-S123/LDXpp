package proj.types;

import proj.env.*;
import proj.ast.ASTNode;

public class ASTTBool implements ASTType {

    public String toString() {
        return "bool";
    }

    public boolean isSubtypeOf(ASTType o, EnvSet e, AlphaEnv alpha) {
        if (o instanceof ASTTId) return isSubtypeOf(e.unfold(o), e, alpha);
        return o instanceof ASTTBool || o instanceof ASTTLBool;
    }

    public boolean defequals(Env<ASTType> sl, ASTType o, Env<ASTType> sr, AlphaEnv alpha) {
        return o instanceof ASTTBool;
    }

    public ASTType inst(String instId, ASTNode n) {
        return this;
    }
}