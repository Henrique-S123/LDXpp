package proj.types;

import proj.env.*;

public class ASTTLInt extends ASTLinType {
    
    public String toString() {
        return "linint";
    }

    public boolean isSubtypeOf(ASTType o, EnvSet e, AlphaEnv alpha) {
        if (o instanceof ASTTId) return isSubtypeOf(e.unfold(o), e, alpha);
        return o instanceof ASTTLInt;
    }

    public boolean defequals(Env<ASTType> sl, ASTType o, Env<ASTType> sr, AlphaEnv alpha) {
        return o instanceof ASTTLInt;
    }
}


