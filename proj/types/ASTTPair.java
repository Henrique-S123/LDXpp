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
        return "(" + first.toStr() + "&" + second.toStr() + ")"; 
    }

    public boolean isSubtypeOf(ASTType o, Environment<ASTType> e) throws InterpreterError {
        return o instanceof ASTTPair;
    }
}
