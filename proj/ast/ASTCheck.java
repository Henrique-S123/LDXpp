package proj.ast;

import proj.values.*;
import proj.types.*;
import proj.env.*;
import proj.errors.*;

public class ASTCheck implements ASTNode {
    ASTNode left, right;

    public ASTCheck(ASTNode l, ASTNode r) {
        left = l;
        right = r;
    }
    
    public IValue eval(Environment<IValue> e) throws InterpreterError {
        return new VBool(true, false);
    }

    public ASTType typecheck(EnvSet e) throws TypeCheckError, InterpreterError {
        ASTType t = left.typecheck(e);
        ASTType t2 = right.typecheck(e);
        if (!t.isSubtypeOf(t2, e) || !t2.isSubtypeOf(t, e))
            throw new TypeCheckError(String.format("terms %s and %s do not have the same type", left, right));
        if (left.normalize().equals(right.normalize())) return new ASTTEq(left, right, t);
        throw new TypeCheckError(String.format("terms %s and %s are not definitionally equal", left, right));
    }

    public ASTNode normalize() {
        return this;
    }
}
