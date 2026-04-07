package proj.types;

import proj.ast.*;
import proj.env.*;
import proj.errors.*;

public class ASTTEq implements ASTType {
    ASTNode term1, term2;
    ASTType type;

    public ASTTEq(ASTNode t1, ASTNode t2, ASTType t) {
        term1 = t1;
        term2 = t2;
        type = t;
    }

    public ASTNode getTerm1() {
        return term1;
    }

    public ASTNode getTerm2() {
        return term2;
    }

    public ASTType getType() {
        return type;
    }

    public String toStr() {
        return term1+" ≡ "+ term2+" : "+type.toStr();
    }

    public boolean isSubtypeOf(ASTType o, EnvSet e) throws InterpreterError {
        return false;
    }

    public boolean equals(Object o) {
        return o instanceof ASTTEq && ((ASTTEq) o).getTerm1().equals(term1)
            && ((ASTTEq) o).getTerm2().equals(term2) && ((ASTTEq) o).getType().equals(type);
    }
}

