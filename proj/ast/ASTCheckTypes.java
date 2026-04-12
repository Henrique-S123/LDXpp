package proj.ast;

import proj.values.*;
import proj.types.*;
import proj.env.*;
import proj.errors.*;

public class ASTCheckTypes implements ASTNode {
    ASTType left, right;

    public ASTCheckTypes(ASTType l, ASTType r) {
        left = l;
        right = r;
    }
    
    public IValue eval(Environment<IValue> e) throws InterpreterError {
        return new VBool(true, false);
    }

    public ASTType typecheck(EnvSet e) throws TypeCheckError, InterpreterError {
        if (left.equals(right)) return new ASTTUnit();
        throw new TypeCheckError(String.format("types %s and %s are not definitionally equal", left.toStr(), right.toStr()));
    }

    public ASTNode normalize(Environment<ASTType> sigma) {
        return this;
    }
}
