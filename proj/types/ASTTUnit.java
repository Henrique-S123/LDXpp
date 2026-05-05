package proj.types;

import proj.env.*;

public class ASTTUnit implements ASTType {

    public String toString() {
        return "()";
    }

    public boolean isSubtypeOf(ASTType o, EnvSet e) {
        if (o instanceof ASTTId) return isSubtypeOf(e.unfold(o), e);
        return o instanceof ASTTUnit;
    }

    public boolean defequals(ASTType o, Env<ASTType> sigma, AlphaEnv alpha) {
        return o instanceof ASTTUnit;
    }
}