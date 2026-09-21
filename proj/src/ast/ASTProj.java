package proj.src.ast;

import proj.src.values.*;
import proj.src.types.*;
import proj.src.env.*;
import proj.src.errors.*;

public class ASTProj extends ASTNode  {
    private final ASTNode pair;
    // true -> fst, false -> snd
    private final boolean pos;

    public ASTProj(ASTNode p, boolean c) {
        pair = p; pos = c;
    }

    public ASTNode getPair() { return pair; }

    public boolean getPos() { return pos; }

    public IValue eval(Env<IValue> e) throws InterpreterError {
        IValue vp = pair.eval(e);
        if (vp instanceof VPair pair) return pos ? pair.getFirst() : pair.getSecond();
        else throw new InterpreterError(ErrorMessages.wrongValueToUnary(pos ? "fst" : "snd", vp));        
    }
    
    public ASTType typecheck(EnvSet e, ASTType target) throws TypeCheckError {
		ASTType tp = pair.typecheck(e, null);
        if (tp instanceof ASTTPair tpair && !tpair.isLinear()) {
            if (pos) return tpair.getFirst();
            return tpair.getSecond().inst(tpair.getBid(), new ASTProj(normalize(e.getSigma()), true).weaknorm());
        }
        else throw new TypeCheckError(ErrorMessages.illegalTypeToUnary(pos ? "fst" : "snd", tp));
	}

    public ASTType puretypecheck(PureEnvSet pe, ASTType target) throws TypeCheckError {
        ASTType tp = pair.puretypecheck(pe, null);
        if (tp instanceof ASTTPair tpair && !tpair.isLinear()) {
            if (pos) return tpair.getFirst();
            return tpair.getSecond().inst(tpair.getBid(), new ASTProj(normalize(pe.getSigma()), true).weaknorm());
        }
        else throw new TypeCheckError(ErrorMessages.illegalTypeToUnary(pos ? "fst" : "snd", tp));
    }

    public ASTNode normalize(Env<ASTType> sig) {
        ASTNode s = pair, finalpair = pair;
        while ((s = s.solve(sig)) != null) finalpair = s;
        return finalpair;
    }
    
    public ASTNode weaknorm(Env<ASTNode> sub) {
        ASTNode pn = pair.weaknorm(sub);
        ASTNode first, second;
        if (pn instanceof ASTPair p) { first = p.getFirst(); second = p.getSecond(); }
        else return new ASTProj(pn, pos);
        return pos ? first.weaknorm(sub) : second.weaknorm(sub);
    }

    public ASTProj solve(Env<ASTType> sigma) {
        ASTNode npair = pair.solve(sigma);
        return (npair == null) ? null : new ASTProj(npair, pos);
    }

    public ASTProj subs(String subsId, ASTNode node) {
        return new ASTProj(pair.subs(subsId, node), pos);
    }

    public boolean structEq(ASTNode o) {
        return o instanceof ASTProj ot && pos == ot.getPos() && pair.structEq(ot.getPair());
    }

    @Override
    public String toString() {
        String s = pos ? "fst %s" : "snd %s";
        return String.format(s, pair);
    }
}
