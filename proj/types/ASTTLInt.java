package proj.types;

import proj.env.*;
import proj.errors.*;

public class ASTTLInt implements ASTLinType {
    
    public String toStr() {
        return "linint";
    }

    public boolean isSubtypeOf(ASTType o, EnvSet e) throws InterpreterError {
        if (o instanceof ASTTId) {
            ASTType to = e.unfold(o);
            return this.isSubtypeOf(to, e);
        }
        return o instanceof ASTTLInt;
    }

    public boolean defequals(ASTType o) {
        return o instanceof ASTTLInt;
    }
}


