package proj.ast;

import proj.values.*;
import proj.types.*;
import proj.env.*;
import proj.env.EnvSet.ENV;
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
    
    public IValue eval(Env<IValue> e) throws InterpreterError {
        IValue v1 = first.eval(e);
        IValue v2 = second.eval(e);
        return new VPair(v1, v2, true);
    }

    public ASTType typecheck(EnvSet e) throws TypeCheckError, EnvironmentError {
        ASTType t1 = first.typecheck(e);
        ASTType t2 = second.typecheck(e);
        return new ASTTTensor(t1, t2, null);
    }

    public ASTType typecheck(EnvSet e, ASTType t) throws TypeCheckError, EnvironmentError {
        ASTType tt1, tt2;
        String ttid;
        if (t instanceof ASTTTensor tensor) { tt1 = tensor.getFirst(); tt2 = tensor.getSecond(); ttid = tensor.getId(); }
        else throw new TypeCheckError("tensor: expected linear pair type");

        e.openEnvScope(ENV.SIGMA);

        ASTType t1 = first.typecheck(e, tt1);
        if (!t1.isSubtypeOf(tt1, e))
            throw new TypeCheckError(String.format("tensor: invalid type %s for first element %s", tt1, first));
        if (ttid != null) e.getEnv(ENV.SIGMA).addEq(new ASTTEq(new ASTId(ttid), first, t1));

        ASTType t2 = second.typecheck(e, tt2);
        if (!t2.isSubtypeOf(tt2, e))
            throw new TypeCheckError(String.format("tensor: invalid type %s for second element %s", tt1, second));
        
        e.closeEnvScope(ENV.SIGMA);
        return t;
    }

    public ASTNode normalize(Env<ASTType> sigma, Env<ASTNode> sub) {
        return new ASTTensor(first.normalize(sigma, sub), second.normalize(sigma, sub));
    }

    public boolean defequals(ASTNode o, Env<ASTType> sigma, AlphaEnv alpha) {
        return o instanceof ASTTensor otensor && first.defequals(otensor.getFirst(), sigma, alpha) &&
            second.defequals(otensor.getSecond(), sigma, alpha);
    }

    @Override
    public String toString() {
        return String.format("(%s | %s)", first, second);
    }
}
