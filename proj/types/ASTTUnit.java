package proj.types;

import proj.env.*;
import proj.errors.*;

public class ASTTUnit implements ASTType {

    public String toStr() {
        return "()";
    }

    public boolean isSubtypeOf(ASTType o, Environment<ASTType> e) throws InterpreterError {
        if (o instanceof ASTTId) {
            ASTType to = e.unfold(o);
            return this.isSubtypeOf(to, e);
        }
        return o instanceof ASTTUnit;
    }
}