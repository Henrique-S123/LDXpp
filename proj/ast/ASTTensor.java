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

    public ASTNode getFirst() {
        return first;
    }

    public ASTNode getSecond() {
        return second;
    }
    
    public IValue eval(Environment<IValue> e) throws InterpreterError {
        IValue v1 = first.eval(e);
        IValue v2 = second.eval(e);
        return new VPair(v1, v2, true);
    }

    public ASTType typecheck(EnvSet e) throws TypeCheckError, EnvironmentError {
        ASTType t1 = first.typecheck(e);
        ASTType t2 = second.typecheck(e);
        return new ASTTTensor(t1, t2);
    }

    public ASTNode normalize(Environment<ASTType> sigma) {
        return new ASTTensor(first.normalize(sigma), second.normalize(sigma));
    }

    public boolean defequals(ASTNode o) {
        return o instanceof ASTTensor &&
            first.defequals(((ASTTensor) o).getFirst()) && second.defequals(((ASTTensor) o).getSecond());
    }

    @Override
    public String toString() {
        return String.format("(%s ⊗ %s)", first.toString(), second.toString());
    }
}
