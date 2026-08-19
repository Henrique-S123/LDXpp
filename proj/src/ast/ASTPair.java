package proj.src.ast;

import proj.src.values.*;
import proj.src.types.*;
import proj.src.env.*;
import proj.src.errors.*;

public class ASTPair extends ASTNode {
    private final ASTNode first, second;
    private final boolean lin;

    public ASTPair(ASTNode f, ASTNode s, boolean l) {
        first = f; second = s; lin = l;
    }

    public ASTNode getFirst() { return first; }

    public ASTNode getSecond() { return second; }

    public boolean isLinear() { return lin; }
    
    public IValue eval(Env<IValue> e) throws InterpreterError {
        IValue v1 = first.eval(e);
        IValue v2 = second.eval(e);
        return new VPair(v1, v2, lin);
    }

    public ASTType typecheck(EnvSet e, ASTType target) throws TypeCheckError {
        ASTType targetfst = null, targetsnd = null;
        String tgtid = null, tgtbid = null;
        if (target != null) {
            ASTType tt = e.unfold(target);
            if (tt instanceof ASTTPair pair && (!lin || pair.isLinear())) {
                targetfst = pair.getFirst(); targetsnd = pair.getSecond(); tgtid = pair.getId(); tgtbid = pair.getBid(); }
            else if (lin) throw new TypeCheckError(ErrorMessages.typeMismatch("tensor", target));
            else throw new TypeCheckError(ErrorMessages.typeMismatch("pair or tensor", target));
        }

        ResourceManager<ASTType> prevDelta = null;
        if (!lin) prevDelta = e.popDelta();

        ASTType t1 = first.typecheck(e, targetfst);
        if (targetfst != null && !t1.isSubtypeOf(targetfst, new PureEnvSet(e)))
            throw new TypeCheckError(ErrorMessages.notSubtype(t1, targetfst));

        if (targetsnd != null && tgtbid != null) targetsnd = targetsnd.inst(tgtbid, first);
        ASTType t2 = second.typecheck(e, targetsnd);
        if (targetsnd != null && !t2.isSubtypeOf(targetsnd, new PureEnvSet(e)))
            throw new TypeCheckError(ErrorMessages.notSubtype(t2, targetsnd));

        if (!lin) e.pushDelta(prevDelta);

        return target == null ? new ASTTPair(t1, t2, tgtid, tgtbid, lin) : target;
    }

    public ASTType puretypecheck(PureEnvSet pe, ASTType target) throws TypeCheckError {
        ASTType targetfst = null, targetsnd = null;
        String tgtid = null, tgtbid = null;
        if (target != null) {
            ASTType tt = pe.unfold(target);
            if (tt instanceof ASTTPair pair && (!lin || pair.isLinear())) {
                targetfst = pair.getFirst(); targetsnd = pair.getSecond(); tgtid = pair.getId(); tgtbid = pair.getBid(); }
            else if (lin) throw new TypeCheckError(ErrorMessages.typeMismatch("tensor", target));
            else throw new TypeCheckError(ErrorMessages.typeMismatch("pair or tensor", target));
        }

        ASTType t1 = first.puretypecheck(pe, targetfst);
        if (targetfst != null && !t1.isSubtypeOf(targetfst, pe))
            throw new TypeCheckError(ErrorMessages.notSubtype(t1, targetfst));

        if (targetsnd != null && tgtbid != null) targetsnd = targetsnd.inst(tgtbid, first);
        ASTType t2 = second.puretypecheck(pe, targetsnd);
        if (targetsnd != null && !t2.isSubtypeOf(targetsnd, pe))
            throw new TypeCheckError(ErrorMessages.notSubtype(t2, targetsnd));
        
        return target == null ? new ASTTPair(t1, t2, tgtid, tgtbid, lin) : target;
    }

    public ASTNode weaknorm(Env<ASTNode> sub) {
        return new ASTPair(first.weaknorm(sub), second.weaknorm(sub), lin);
    }

    public ASTNode subs(String subsId, ASTNode node) {
        return new ASTPair(first.subs(subsId, node), second.subs(subsId, node), lin);
    }

    @Override
    public String toString() {
        return String.format("(%s%s %s)", first, (lin) ? " |" : ",", second);
    }
}
