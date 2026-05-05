package proj.types;

import proj.env.*;

public class ASTTInt implements ASTType {
    
    public String toString() {
        return "int";
    }

    public boolean isSubtypeOf(ASTType o, EnvSet e) {
        if (o instanceof ASTTId) {
            ASTType to = e.unfold(o);
            return this.isSubtypeOf(to, e);
        }
        return o instanceof ASTTInt || o instanceof ASTTLInt;
    }

    public boolean defequals(ASTType o, Env<ASTType> sigma, AlphaEnv alpha) {
        return o instanceof ASTTInt;
    }
}


