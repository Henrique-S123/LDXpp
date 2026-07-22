package proj.src.ast;

import proj.src.values.*;
import proj.src.types.*;
import proj.src.env.*;
import proj.src.errors.*;

public class ASTChoice extends ASTNode  {
    private final ASTNode pair;
    // true -> fst, false -> snd
    private final boolean choice;

    public ASTChoice(ASTNode p, boolean c) {
        pair = p;
        choice = c;
    }

    public ASTNode getPair() {
        return pair;
    }

    public boolean getChoice() {
        return choice;
    }

    public IValue eval(Env<IValue> e) throws InterpreterError {
        IValue vp = pair.eval(e);
        if (vp instanceof VPair pair) {
            return choice ? pair.getFirst() : pair.getSecond();
        } else {
            throw new InterpreterError(ErrorMessages.wrongValueToUnary(choice ? "fst" : "snd", vp));
        }           
    }
    
    public ASTType typecheck(EnvSet e, ASTType target) throws TypeCheckError {
		ASTType tp = pair.typecheck(e, null);
        this.setSig(e.getSigma());
        if (tp instanceof ASTTPair tpair)
            return choice ? tpair.getFirst() : tpair.getSecond().inst(tpair.getId(), new ASTChoice(pair, true).normalize(e.getSigma()));
        else throw new TypeCheckError(ErrorMessages.illegalTypeToUnary(choice ? "fst" : "snd", tp));
	}

    public ASTType puretypecheck(Env<ASTType> sigma, Env<ASTType> phi, AlphaEnv alpha, ASTType target) throws TypeCheckError {
        ASTType tp = pair.puretypecheck(sigma, phi, alpha, null);
        this.setSig(sigma);
        if (tp instanceof ASTTPair tpair)
            return choice ? tpair.getFirst() : tpair.getSecond().inst(tpair.getId(), new ASTChoice(pair, true).normalize(sigma));
        else throw new TypeCheckError(ErrorMessages.illegalTypeToUnary(choice ? "fst" : "snd", tp));
    }
    
    public ASTNode weaknorm(Env<ASTNode> sub) {
        ASTNode pn = pair.weaknorm(sub);
        ASTNode first, second;
        if (pn instanceof ASTPair p) { first = p.getFirst(); second = p.getSecond(); }
        else return new ASTChoice(pn, choice);
        return choice ? first.weaknorm(sub) : second.weaknorm(sub);
    }

    public ASTChoice solve(Env<ASTType> sigma) {
        ASTNode npair = pair.solve(sigma);
        return (npair == null) ? null : new ASTChoice(npair, choice);
    }

    public ASTChoice subs(String subsId, ASTNode node) {
        return new ASTChoice(pair.subs(subsId, node), choice);
    }

    @Override
    public String toString() {
        String s = choice ? "fst %s" : "snd %s";
        return String.format(s, pair);
    }
}
