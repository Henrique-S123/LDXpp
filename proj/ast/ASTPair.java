package proj.ast;

import proj.values.*;
import proj.types.*;
import proj.env.*;
import proj.env.EnvSet.ENV;
import proj.errors.*;

public class ASTPair extends ASTNode {
    ASTNode first, second;
    Env<ASTType> sig;

    public ASTPair(ASTNode f, ASTNode s) {
        first = f;
        second = s;
    }
    
    public ASTPair(ASTNode f, ASTNode s, Env<ASTType> si) {
        first = f;
        second = s;
        sig = si;
    }

    public ASTNode getFirst() {
        return first;
    }

    public ASTNode getSecond() {
        return second;
    }

    public Env<ASTType> getSig() {
        return sig;
    }

    public void setSig(Env<ASTType> s) {
        sig = s;
    }
    
    public IValue eval(Env<IValue> e) throws InterpreterError {
        IValue v1 = first.eval(e);
        IValue v2 = second.eval(e);
        return new VPair(v1, v2, false);
    }

    public ASTType typecheck(EnvSet e) throws TypeCheckError, EnvironmentError {
        Env<ASTType> prevDelta = e.popDelta();
        ASTType t1 = first.typecheck(e);
        ASTType t2 = second.typecheck(e);
        e.setEnv(ENV.DELTA, prevDelta);
        setSig(e.getEnv(ENV.SIGMA));
        return new ASTTPair(t1, t2, null);
    }

    public ASTType typecheck(EnvSet e, ASTType t) throws TypeCheckError, EnvironmentError {
        ASTType tgt1, tgt2;
        String tgtid;
        if (t instanceof ASTTPair pair) { tgt1 = pair.getFirst(); tgt2 = pair.getSecond(); tgtid = pair.getId(); }
        else if (t instanceof ASTTTensor tensor) { tgt1 = tensor.getFirst(); tgt2 = tensor.getSecond(); tgtid = tensor.getId(); }
        else throw new TypeCheckError(ErrorMessages.typeMismatch("pair or tensor", t));

        Env<ASTType> prevDelta = e.popDelta();
        e.openEnvScope(ENV.SIGMA);

        ASTType t1 = first.typecheck(e, tgt1);
        if (!t1.isSubtypeOf(tgt1, e, new AlphaEnv())) throw new TypeCheckError(ErrorMessages.notSubtype(t1, tgt1));

        ASTType insttgt2 = (tgtid != null) ? tgt2.inst(tgtid, first) : tgt2;
        ASTType t2 = second.typecheck(e, insttgt2);
        if (!t2.isSubtypeOf(insttgt2, e, new AlphaEnv())) throw new TypeCheckError(ErrorMessages.notSubtype(t2, tgt2));

        e.closeEnvScope(ENV.SIGMA);
        e.setEnv(ENV.DELTA, prevDelta);
        setSig(e.getEnv(ENV.SIGMA));
        return new ASTTPair(tgt1, tgt2, tgtid);
    }

    public ASTNode weaknorm(Env<ASTNode> sub) {
        return new ASTPair(first.weaknorm(sub), second.weaknorm(sub), sig);
    }

    public ASTNode solve(Env<ASTType> sigma) {
        ASTNode nfirst = first.solve(getSig());
        if (nfirst != null) return new ASTPair(nfirst, second, sig);
        ASTNode nsecond = second.solve(getSig());
        if (nsecond != null) return new ASTPair(first, nsecond, sig);
        return null;
    }

    public ASTNode subs(String subsId, ASTNode node) {
        return new ASTPair(first.subs(subsId, node), second.subs(subsId, node), sig);
    }

    public boolean defequals(ASTNode o, Env<ASTType> sigma, AlphaEnv alpha) {
        return o instanceof ASTPair opair && first.defequals(opair.getFirst(), sigma, alpha)
            && second.defequals(opair.getSecond(), sigma, alpha);
    }

    @Override
    public String toString() {
        return String.format("(%s, %s)", first, second);
    }
}
