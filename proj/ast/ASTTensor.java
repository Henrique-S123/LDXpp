package proj.ast;

import proj.values.*;
import proj.types.*;
import proj.env.*;
import proj.errors.*;

public class ASTTensor implements ASTNode {
    ASTNode first, second;

    public ASTTensor(ASTNode f, ASTNode s) {
        first = f;
        second = s;
    }
    
    public IValue eval(Environment<IValue> e) throws InterpreterError {
        IValue v1 = first.eval(e);
        IValue v2 = second.eval(e);
        return new VPair(v1, v2, true);
    }

    public ASTType typecheck(Environment<ASTType> e) throws TypeCheckError, InterpreterError {
        ASTType t1 = first.typecheck(e);
        ASTType t2 = second.typecheck(e);
        return new ASTTTensor(t1, t2);
    }
}
