package proj.types;

import proj.ast.ASTNode;
import proj.env.*;

public class ASTTInt implements ASTType {
    
    public String toString() {
        return "int";
    }

    public boolean isSubtypeOf(ASTType o, EnvSet e) {
        if (o instanceof ASTTId) return isSubtypeOf(e.unfold(o), e);
        return o instanceof ASTTInt || o instanceof ASTTLInt;
    }

    public boolean defequals(ASTType o, Env<ASTType> sigma, AlphaEnv alpha) {
        return o instanceof ASTTInt;
    }

    public ASTType inst(String instId, ASTNode n) {
        return this;
    }
}


