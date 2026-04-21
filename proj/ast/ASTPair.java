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
        if (!(t instanceof ASTTPair))
            throw new TypeCheckError("pair: expected cartesian pair type");
        ASTTPair tt = ((ASTTPair) t);
        Environment<ASTType> prevDelta = e.popDelta();
        e.openEnvScope(ENV.SIGMA);

        ASTType t1 = first.typecheck(e);
        if (!(t1).defequals(tt.getFirst(), e.getEnv(ENV.SIGMA)))
            throw new TypeCheckError("pair: invalid type for first element");
        e.getEnv(ENV.SIGMA).addEq(new ASTTEq(new ASTId(tt.getId()), first, t1));

        ASTType t2 = second.typecheck(e, tt.getSecond());
        if (!(t2.defequals(tt.getSecond(), e.getEnv(ENV.SIGMA))))
            throw new TypeCheckError("pair: invalid type for second element");
        
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
