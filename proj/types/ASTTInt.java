package proj.types;

import proj.ast.*;
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

    public boolean defequals(ASTType o, Environment<ASTType> sigma, Environment<ASTNode> alphaL, Environment<ASTNode> alphaR) {
        return o instanceof ASTTInt;
    }
}


