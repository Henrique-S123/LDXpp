package proj.types;

import proj.env.*;

public class ASTTLBool implements ASTLinType {

    public String toString() {
        return "linbool";
    }

    public boolean isSubtypeOf(ASTType o, EnvSet e) {
        if (o instanceof ASTTId) {
            ASTType to = e.unfold(o);
            return this.isSubtypeOf(to, e);
        }
        return o instanceof ASTTLBool;
    }

    public boolean defequals(ASTType o, Env<ASTType> sigma, AlphaEnv alpha) {
        return o instanceof ASTTLBool;
    }
}