package proj.ast;

import proj.values.*;
import proj.types.*;
import proj.env.*;
import proj.errors.*;

public class ASTTensor extends ASTNode {
    ASTNode first, second;
    Env<ASTType> sig;

    public ASTTensor(ASTNode f, ASTNode s) {
        first = f;
        second = s;
    }

    public ASTTensor(ASTNode f, ASTNode s, Env<ASTType> si) {
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
        return new VPair(v1, v2, true);
    }

    public ASTType typecheck(EnvSet e, ASTType target) throws TypeCheckError {
        ASTType targetfst = null, targetsnd = null;
        String tgtid = null;
        if (target != null) {
            ASTType tt = e.unfold(target);
            if (tt instanceof ASTTTensor tensor) { targetfst = tensor.getFirst(); targetsnd = tensor.getSecond(); tgtid = tensor.getId(); }
            else throw new TypeCheckError(ErrorMessages.typeMismatch("tensor", target));
        }

        ASTType t1 = first.typecheck(e, targetfst);
        if (targetfst != null && !t1.isSubtypeOf(targetfst, e.getSigma(), e.getPhi(), new AlphaEnv()))
            throw new TypeCheckError(ErrorMessages.notSubtype(t1, targetfst));

        ASTType insttgt2 = (tgtid != null) ? targetsnd.inst(tgtid, first) : targetsnd;
        ASTType t2 = second.typecheck(e, insttgt2);
        if (targetsnd != null && !t2.isSubtypeOf(insttgt2, e.getSigma(), e.getPhi(), new AlphaEnv()))
            throw new TypeCheckError(ErrorMessages.notSubtype(t2, targetsnd));
        
        setSig(e.getSigma());
        return new ASTTTensor(targetfst == null ? t1 : targetfst, targetsnd == null ? t2 : targetsnd, tgtid);
    }

    public ASTType puretypecheck(Env<ASTType> sigma, Env<ASTType> phi, ASTType target) throws TypeCheckError {
        ASTType targetfst = null, targetsnd = null;
        String tgtid = null;
        if (target != null) {
            ASTType tt = phi.unfold(target);
            if (tt instanceof ASTTTensor tensor) { targetfst = tensor.getFirst(); targetsnd = tensor.getSecond(); tgtid = tensor.getId(); }
            else throw new TypeCheckError(ErrorMessages.typeMismatch("tensor", target));
        }

        ASTType t1 = first.puretypecheck(sigma, phi, targetfst);
        if (targetfst != null && !t1.isSubtypeOf(targetfst, sigma, phi, new AlphaEnv()))
            throw new TypeCheckError(ErrorMessages.notSubtype(t1, targetfst));

        ASTType insttgt2 = (tgtid != null) ? targetsnd.inst(tgtid, first) : targetsnd;
        ASTType t2 = second.puretypecheck(sigma, phi, insttgt2);
        if (targetsnd != null && !t2.isSubtypeOf(insttgt2, sigma, phi, new AlphaEnv()))
            throw new TypeCheckError(ErrorMessages.notSubtype(t2, targetsnd));

        return new ASTTTensor(targetfst == null ? t1 : targetfst, targetsnd == null ? t2 : targetsnd, tgtid);
    }

    public ASTNode weaknorm(Env<ASTNode> sub) {
        return new ASTTensor(first.weaknorm(sub), second.weaknorm(sub), sig);
    }

    public ASTNode subs(String subsId, ASTNode node) {
        return new ASTTensor(first.subs(subsId, node), second.subs(subsId, node), sig);
    }

    @Override
    public String toString() {
        return String.format("(%s | %s)", first, second);
    }
}
