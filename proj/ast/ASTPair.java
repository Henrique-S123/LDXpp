package proj.ast;

import proj.values.*;
import proj.types.*;
import proj.env.*;
import proj.errors.*;

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
        String tgtid = null;
        if (target != null) {
            ASTType tt = e.unfold(target);
            if (tt instanceof ASTTPair pair && (!lin || pair.isLinear())) { targetfst = pair.getFirst(); targetsnd = pair.getSecond(); tgtid = pair.getId(); }
            else if (lin) throw new TypeCheckError(ErrorMessages.typeMismatch("tensor", target));
            else throw new TypeCheckError(ErrorMessages.typeMismatch("pair or tensor", target));
        }

        ResourceManager<ASTType> prevDelta = null;
        if (!lin) prevDelta = e.popDelta();

        first.setSig(e.getSigma());
        ASTType t1 = first.typecheck(e, targetfst);
        if (targetfst != null && !t1.isSubtypeOf(targetfst, e.getSigma(), e.getPhi(), e.getAlpha()))
            throw new TypeCheckError(ErrorMessages.notSubtype(t1, targetfst));

        second.setSig(e.getSigma());
        ASTType insttgt2 = (tgtid != null) ? targetsnd.inst(tgtid, first) : targetsnd;
        ASTType t2 = second.typecheck(e, insttgt2);
        if (targetsnd != null && !t2.isSubtypeOf(insttgt2, e.getSigma(), e.getPhi(), e.getAlpha()))
            throw new TypeCheckError(ErrorMessages.notSubtype(t2, targetsnd));

        if (!lin) e.pushDelta(prevDelta);

        ASTType firsttype = targetfst == null ? t1 : targetfst;
        ASTType secondtype = targetsnd == null ? t2 : targetsnd;
        return new ASTTPair(firsttype, secondtype, tgtid, lin);
    }

    public ASTType puretypecheck(Env<ASTType> sigma, Env<ASTType> phi, AlphaEnv alpha, ASTType target) throws TypeCheckError {
        ASTType targetfst = null, targetsnd = null;
        String tgtid = null;
        if (target != null) {
            ASTType tt = phi.unfold(target);
            if (tt instanceof ASTTPair pair && (!lin || pair.isLinear())) { targetfst = pair.getFirst(); targetsnd = pair.getSecond(); tgtid = pair.getId(); }
            else if (lin) throw new TypeCheckError(ErrorMessages.typeMismatch("tensor", target));
            else throw new TypeCheckError(ErrorMessages.typeMismatch("pair or tensor", target));
        }

        first.setSig(sigma);
        ASTType t1 = first.puretypecheck(sigma, phi, alpha, targetfst);
        if (targetfst != null && !t1.isSubtypeOf(targetfst, sigma, phi, alpha))
            throw new TypeCheckError(ErrorMessages.notSubtype(t1, targetfst));

        second.setSig(sigma);
        ASTType insttgt2 = (tgtid != null) ? targetsnd.inst(tgtid, first) : targetsnd;
        ASTType t2 = second.puretypecheck(sigma, phi, alpha, insttgt2);
        if (targetsnd != null && !t2.isSubtypeOf(insttgt2, sigma, phi, alpha))
            throw new TypeCheckError(ErrorMessages.notSubtype(t2, targetsnd));
        
        ASTType firsttype = targetfst == null ? t1 : targetfst;
        ASTType secondtype = targetsnd == null ? t2 : targetsnd;
        return new ASTTPair(firsttype, secondtype, tgtid, lin);
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
