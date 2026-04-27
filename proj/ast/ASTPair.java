package proj.ast;

import proj.values.*;
import proj.types.*;
import proj.env.*;
import proj.env.EnvSet.ENV;
import proj.errors.*;

public class ASTPair implements ASTNode {
    ASTNode first, second;

    public ASTPair(ASTNode f, ASTNode s) {
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
        return new VPair(v1, v2, false);
    }

    public ASTType typecheck(EnvSet e) throws TypeCheckError, EnvironmentError {
        Environment<ASTType> prevDelta = e.popDelta();
        ASTType t1 = first.typecheck(e);
        ASTType t2 = second.typecheck(e);
        e.setEnv(ENV.DELTA, prevDelta);
        return new ASTTPair(t1, t2, null);
    }

    public ASTType typecheck(EnvSet e, ASTType t) throws TypeCheckError, EnvironmentError {
        ASTType tt1, tt2;
        String ttid;
        if (t instanceof ASTTPair pair) { tt1 = pair.getFirst(); tt2 = pair.getSecond(); ttid = pair.getId(); }
        else if (t instanceof ASTTTensor tensor) { tt1 = tensor.getFirst(); tt2 = tensor.getSecond(); ttid = tensor.getId(); }
        else throw new TypeCheckError("pair: expected pair type");

        Environment<ASTType> prevDelta = e.popDelta();
        e.openEnvScope(ENV.SIGMA);

        ASTType t1 = first.typecheck(e, tt1);
        if (!t1.isSubtypeOf(tt1, e))
            throw new TypeCheckError(String.format("pair: invalid type %s for first element %s", tt1.toStr(), first.toString()));
        e.addEq(new ASTTEq(new ASTId(ttid), first, t1));

        ASTType t2 = second.typecheck(e, tt2);
        if (!t2.isSubtypeOf(tt2, e))
            throw new TypeCheckError(String.format("pair: invalid type %s for second element %s", tt2.toStr(), second.toString()));

        e.closeEnvScope(ENV.SIGMA);
        e.setEnv(ENV.DELTA, prevDelta);
        return t;
    }

    public ASTNode normalize(Environment<ASTType> sigma) {
        return new ASTPair(first.normalize(sigma), second.normalize(sigma));
    }

    public boolean defequals(ASTNode o, Environment<ASTType> sigma) {
        return o instanceof ASTPair &&
            first.defequals(((ASTPair) o).getFirst(), sigma) && second.defequals(((ASTPair) o).getSecond(), sigma);
    }

    @Override
    public String toString() {
        return String.format("(%s, %s)", first.toString(), second.toString());
    }
}
