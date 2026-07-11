package proj.ast;

import proj.values.*;
import proj.types.*;
import proj.env.*;
import proj.errors.*;

public class ASTPair extends ASTNode {
    private final ASTNode first, second;

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
    
    public IValue eval(Env<IValue> e) throws InterpreterError {
        IValue v1 = first.eval(e);
        IValue v2 = second.eval(e);
        return new VPair(v1, v2, false);
    }

    public ASTType typecheck(EnvSet e, ASTType target) throws TypeCheckError {
        ASTType targetfst = null, targetsnd = null;
        String tgtid = null;
        if (target != null) {
            ASTType tt = e.unfold(target);
            if (tt instanceof ASTTPair pair) { targetfst = pair.getFirst(); targetsnd = pair.getSecond(); tgtid = pair.getId(); }
            else if (tt instanceof ASTTTensor tensor) { targetfst = tensor.getFirst(); targetsnd = tensor.getSecond(); tgtid = tensor.getId(); }
            else throw new TypeCheckError(ErrorMessages.typeMismatch("pair or tensor", target));
        }

        Env<LinearBinding> prevDelta = e.popDelta();

        first.setSig(e.getSigma());
        ASTType t1 = first.typecheck(e, targetfst);
        if (targetfst != null && !t1.isSubtypeOf(targetfst, e.getSigma(), e.getPhi(), e.getAlpha()))
            throw new TypeCheckError(ErrorMessages.notSubtype(t1, targetfst));

        second.setSig(e.getSigma());
        ASTType insttgt2 = (tgtid != null) ? targetsnd.inst(tgtid, first) : targetsnd;
        ASTType t2 = second.typecheck(e, insttgt2);
        if (targetsnd != null && !t2.isSubtypeOf(insttgt2, e.getSigma(), e.getPhi(), e.getAlpha()))
            throw new TypeCheckError(ErrorMessages.notSubtype(t2, targetsnd));

        e.pushDelta(prevDelta);
        return new ASTTPair(targetfst == null ? t1 : targetfst, targetsnd == null ? t2 : targetsnd, tgtid);
    }

    public ASTType puretypecheck(Env<ASTType> sigma, Env<ASTType> phi, AlphaEnv alpha, ASTType target) throws TypeCheckError {
        ASTType targetfst = null, targetsnd = null;
        String tgtid = null;
        if (target != null) {
            ASTType tt = phi.unfold(target);
            if (tt instanceof ASTTPair pair) { targetfst = pair.getFirst(); targetsnd = pair.getSecond(); tgtid = pair.getId(); }
            else if (tt instanceof ASTTTensor tensor) { targetfst = tensor.getFirst(); targetsnd = tensor.getSecond(); tgtid = tensor.getId(); }
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
        
        return new ASTTPair(targetfst == null ? t1 : targetfst, targetsnd == null ? t2 : targetsnd, tgtid);
    }

    public ASTNode weaknorm(Env<ASTNode> sub) {
        return new ASTPair(first.weaknorm(sub), second.weaknorm(sub));
    }

    public ASTNode subs(String subsId, ASTNode node) {
        return new ASTPair(first.subs(subsId, node), second.subs(subsId, node));
    }

    @Override
    public String toString() {
        return String.format("(%s, %s)", first, second);
    }
}
