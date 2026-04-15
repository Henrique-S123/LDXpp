package proj.types;

import proj.env.*;

public class ASTTBool implements ASTType {

    public String toStr() {
        return "bool";
    }

    public boolean isSubtypeOf(ASTType o, EnvSet e) {
        if (o instanceof ASTTId) {
            ASTType to = e.unfold(o);
            return this.isSubtypeOf(to, e);
        }
        return o instanceof ASTTBool || o instanceof ASTTLBool;
    }

    public boolean defequals(ASTType o, Environment<ASTType> sigma) {
        return o instanceof ASTTBool;
    }
}