package proj.types;

import proj.env.*;
import proj.errors.*;

public class ASTTPair implements ASTType {
    ASTType first, second;

    public ASTTPair(ASTType f, ASTType s) {
        first = f;
        second = s;
    }

    public ASTType getFirst() {
        return first;
    }
    
    public ASTType getSecond() {
        return second;
    }

    public String toStr() {
        return "(" + first.toStr() + ", " + second.toStr() + ")"; 
    }

    public boolean isSubtypeOf(ASTType o, Environment<ASTType> e) throws InterpreterError {
        if (o instanceof ASTTId) {
            ASTType to = e.unfold(o);
            return this.isSubtypeOf(to, e);
        } else if (o instanceof ASTTPair) {
            return first.isSubtypeOf(((ASTTPair) o).getFirst(), e)
                && second.isSubtypeOf(((ASTTPair) o).getSecond(), e);
        } else if (o instanceof ASTTTensor) {
            return first.isSubtypeOf(((ASTTTensor) o).getFirst(), e)
                && second.isSubtypeOf(((ASTTTensor) o).getSecond(), e);
        }
        return false;
    }
}
