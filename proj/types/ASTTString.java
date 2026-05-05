package proj.types;

import proj.ast.ASTNode;
import proj.env.*;

public class ASTTString implements ASTType {

    public String toString() {
        return "string";
    }

    public boolean isSubtypeOf(ASTType o, EnvSet e) {
        if (o instanceof ASTTId) {
            ASTType to = e.unfold(o);
            return this.isSubtypeOf(to, e);
        }
        return o instanceof ASTTString;
    }

    public boolean defequals(ASTType o, Environment<ASTType> sigma, Environment<ASTNode> alphaL, Environment<ASTNode> alphaR) {
        return o instanceof ASTTString;
    }
}
