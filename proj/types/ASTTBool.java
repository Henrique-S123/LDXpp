package proj.types;

import proj.ast.*;
import proj.env.*;

public class ASTTBool implements ASTType {

    public String toString() {
        return "bool";
    }

    public boolean isSubtypeOf(ASTType o, EnvSet e) {
        if (o instanceof ASTTId) {
            ASTType to = e.unfold(o);
            return this.isSubtypeOf(to, e);
        }
        return o instanceof ASTTBool || o instanceof ASTTLBool;
    }

    public boolean defequals(ASTType o, Env<ASTType> sigma, Env<ASTNode> alphaL, Env<ASTNode> alphaR) {
        return o instanceof ASTTBool;
    }
}