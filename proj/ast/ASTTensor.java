package proj.ast;

import proj.values.*;
import proj.types.*;
import proj.env.*;
import proj.env.EnvSet.ENV;
import proj.errors.*;

public class ASTTensor extends ASTNode {
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
        return new ASTTTensor(t1, t2, null, e.getEnv(ENV.SIGMA));
    }

    public ASTType typecheck(EnvSet e, ASTType t) throws TypeCheckError, EnvironmentError {
        ASTType tgt1, tgt2;
        String tgtid;
        if (t instanceof ASTTTensor tensor) { tgt1 = tensor.getFirst(); tgt2 = tensor.getSecond(); tgtid = tensor.getId(); }
        else throw new TypeCheckError("tensor: expected linear pair type");

        e.openEnvScope(ENV.SIGMA);

        ASTType t1 = first.typecheck(e, tgt1);
        if (!t1.isSubtypeOf(tgt1, e))
            throw new TypeCheckError(String.format("tensor: invalid type %s for first element %s", tgt1, first));

        ASTType insttgt2 = (tgtid != null) ? tgt2.inst(tgtid, first) : tgt2;
        ASTType t2 = second.typecheck(e, insttgt2);
        if (!t2.isSubtypeOf(insttgt2, e))
            throw new TypeCheckError(String.format("tensor: invalid type %s for second element %s", tgt2, second));
        
        e.closeEnvScope(ENV.SIGMA);
        return new ASTTTensor(tgt1, tgt2, tgtid, e.getEnv(ENV.SIGMA));
    }

    public ASTNode normalize(Env<ASTType> sigma, Env<ASTNode> sub) {
        return new ASTTensor(first.normalize(sigma, sub), second.normalize(sigma, sub));
    }

    public ASTNode solve(Env<ASTType> sigma) {
        ASTNode nfirst = first.solve(sigma);
        if (nfirst != null) return new ASTTensor(nfirst, second);
        ASTNode nsecond = second.solve(sigma);
        if (nsecond != null) return new ASTTensor(first, nsecond);
        return null;
    }

    public ASTNode subs(String subsId, ASTNode node) {
        return new ASTTensor(first.subs(subsId, node), second.subs(subsId, node));
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
