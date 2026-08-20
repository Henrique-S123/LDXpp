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
        ASTType tgtfst = null, tgtsnd = null;
        String tgtid = null, tgtbid = null;
        if (target != null) {
            ASTType tt = e.unfold(target);
            if (tt instanceof ASTTPair pair && (!lin || pair.isLinear())) {
                tgtfst = pair.getFirst(); tgtsnd = pair.getSecond(); tgtid = pair.getId(); tgtbid = pair.getBid(); }
            else if (lin) throw new TypeCheckError(ErrorMessages.typeMismatch("tensor", target));
            else throw new TypeCheckError(ErrorMessages.typeMismatch("pair or tensor", target));
        }

        ResourceManager<ASTType> prevDelta = null;
        if (!lin) prevDelta = e.popDelta();

        ASTType t1 = first.typecheck(e, tgtfst);
        if (tgtfst != null && !t1.isSubtypeOf(tgtfst, new PureEnvSet(e)))
            throw new TypeCheckError(ErrorMessages.notSubtype(t1, tgtfst));

        if (tgtsnd != null && tgtbid != null) tgtsnd = tgtsnd.inst(tgtbid, first);
        ASTType t2 = second.typecheck(e, tgtsnd);
        if (tgtsnd != null && !t2.isSubtypeOf(tgtsnd, new PureEnvSet(e)))
            throw new TypeCheckError(ErrorMessages.notSubtype(t2, tgtsnd));

        if (!lin) e.pushDelta(prevDelta);

        return target == null ? new ASTTPair(t1, t2, tgtid, tgtbid, lin) : target;
    }

    public ASTType puretypecheck(PureEnvSet pe, ASTType target) throws TypeCheckError {
        ASTType tgtfst = null, tgtsnd = null;
        String tgtid = null, tgtbid = null;
        if (target != null) {
            ASTType tt = pe.unfold(target);
            if (tt instanceof ASTTPair pair && (!lin || pair.isLinear())) {
                tgtfst = pair.getFirst(); tgtsnd = pair.getSecond(); tgtid = pair.getId(); tgtbid = pair.getBid(); }
            else if (lin) throw new TypeCheckError(ErrorMessages.typeMismatch("tensor", target));
            else throw new TypeCheckError(ErrorMessages.typeMismatch("pair or tensor", target));
        }

        ASTType t1 = first.puretypecheck(pe, tgtfst);
        if (tgtfst != null && !t1.isSubtypeOf(tgtfst, pe))
            throw new TypeCheckError(ErrorMessages.notSubtype(t1, tgtfst));

        if (tgtsnd != null && tgtbid != null) tgtsnd = tgtsnd.inst(tgtbid, first);
        ASTType t2 = second.puretypecheck(pe, tgtsnd);
        if (tgtsnd != null && !t2.isSubtypeOf(tgtsnd, pe))
            throw new TypeCheckError(ErrorMessages.notSubtype(t2, tgtsnd));
        
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
