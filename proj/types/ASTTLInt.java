package proj.types;

import proj.ast.ASTNode;
import proj.env.*;

public class ASTTLInt implements ASTLinType {
    
    public String toString() {
        return "linint";
    }

    public boolean isSubtypeOf(ASTType o, EnvSet e) {
        if (o instanceof ASTTId) {
            ASTType to = e.unfold(o);
            return this.isSubtypeOf(to, e);
        }
        return o instanceof ASTTLInt;
    }

    public boolean defequals(ASTType o, Env<ASTType> sigma, Env<ASTNode> alphaL, Env<ASTNode> alphaR) {
        return o instanceof ASTTLInt;
    }
}


