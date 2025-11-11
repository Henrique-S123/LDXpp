package proj.types;

import proj.env.*;
import proj.errors.*;

public class ASTTBool implements ASTType {

    public String toStr() {
        return "bool";
    }

    public boolean isSubtypeOf(ASTType o, Environment<ASTType> e) throws InterpreterError {
        if (o instanceof ASTTId) {
            ASTType to = e.unfold(o);
            return this.isSubtypeOf(to, e);
        }
        return o instanceof ASTTBool || o instanceof ASTTLBool;
    }
}