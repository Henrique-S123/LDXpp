package proj.types;

import proj.ast.ASTNode;
import proj.env.*;

public class ASTTUnit implements ASTType {

    public String toString() {
        return "()";
    }

    public boolean isSubtypeOf(ASTType o, EnvSet e) {
        if (o instanceof ASTTId) {
            ASTType to = e.unfold(o);
            return this.isSubtypeOf(to, e);
        }
        return o instanceof ASTTUnit;
    }

    public boolean defequals(ASTType o, Environment<ASTType> sigma, Environment<ASTNode> alphaL, Environment<ASTNode> alphaR) {
        return o instanceof ASTTUnit;
    }
}