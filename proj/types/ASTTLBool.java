package proj.types;

import proj.ast.ASTNode;
import proj.env.*;

public class ASTTLBool implements ASTLinType {

    public String toString() {
        return "linbool";
    }

    public boolean isSubtypeOf(ASTType o, EnvSet e, AlphaEnv alpha) {
        if (o instanceof ASTTId) return isSubtypeOf(e.unfold(o), e, alpha);
        return o instanceof ASTTLBool;
    }

    public boolean defequals(ASTType o, Env<ASTType> sigma, AlphaEnv alpha) {
        return o instanceof ASTTLBool;
    }

    public ASTType inst(String instId, ASTNode n) {
        return this;
    }
}