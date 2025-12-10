package proj.types;

import proj.env.*;
import proj.errors.*;

public class ASTTTensor implements ASTLinType {
    ASTType first, second;

    public ASTTTensor(ASTType f, ASTType s) {
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
        return "(" + first.toStr() + " | " + second.toStr() + ")";
    }

    public boolean isSubtypeOf(ASTType o, EnvSet e) throws InterpreterError {
        if (o instanceof ASTTId) {
            ASTType to = e.unfold(o);
            return this.isSubtypeOf(to, e);
        } else if (o instanceof ASTTTensor) {
            return first.isSubtypeOf(((ASTTTensor) o).getFirst(), e)
                && second.isSubtypeOf(((ASTTTensor) o).getSecond(), e);
        }
        return false;
    }
}
