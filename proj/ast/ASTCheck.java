package proj.ast;

import proj.values.*;
import proj.types.*;
import proj.env.*;
import proj.errors.*;
import proj.Equalizer;

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
        if (Equalizer.defeq(left, right) || Equalizer.defeq(right, left)) return new ASTTEq(left, right, t);
        throw new TypeCheckError("the two terms are not definitionally equal");
    }
}
