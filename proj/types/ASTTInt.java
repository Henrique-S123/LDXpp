package proj.types;

import proj.env.*;
import proj.errors.*;

public class ASTTInt implements ASTType {
    
    public String toStr() {
        return "int";
    }

    public boolean isSubtypeOf(ASTType o, EnvSet e) throws InterpreterError {
        if (o instanceof ASTTId) {
            ASTType to = e.unfold(o);
            return this.isSubtypeOf(to, e);
        }
        return o instanceof ASTTInt || o instanceof ASTTLInt;
    }

    public boolean equals(Object o) {
        return o instanceof ASTTInt;
    }
}


