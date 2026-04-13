package proj.types;

import proj.env.*;
import proj.errors.*;

public class ASTTPair implements ASTType {
    ASTType first, second;
    String id;

    public ASTTPair(ASTType f, ASTType s, String i) {
        first = f;
        second = s;
        id = i;
    }

    public ASTType getFirst() {
        return first;
    }
    
    public ASTType getSecond() {
        return second;
    }

    public String getId() {
        return id;
    }

    public String toStr() {
        return "(" + (id != null ? id + ":" : "") + first.toStr() + ", " + second.toStr() + ")";
    }

    public boolean isSubtypeOf(ASTType o, EnvSet e) throws InterpreterError {
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

    public boolean defequals(ASTType o) {
        return o instanceof ASTTPair && ((ASTTPair) o).getFirst().defequals(first)
            && ((ASTTPair) o).getSecond().defequals(second);
    }
}
