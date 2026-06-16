package proj.ast;

import proj.values.*;
import proj.types.*;
import proj.defeq.TermClosure;
import proj.env.*;
import proj.errors.*;

public class ASTChoice extends ASTNode  {
    ASTNode pair;
    int choice;

    public ASTChoice(ASTNode p, int c) {
        pair = p;
        choice = c;
    }

    public ASTNode getPair() {
        return pair;
    }

    public int getChoice() {
        return choice;
    }

    public IValue eval(Env<IValue> e) throws InterpreterError {
        IValue vp = pair.eval(e);
        if (vp instanceof VPair pair) {
            return choice > 0 ? pair.getSecond() : pair.getFirst();
        } else {
            throw new InterpreterError(ErrorMessages.wrongValueToUnary(choice > 0 ? "snd" : "fst", vp));
        }           
    }
    
    public ASTType typecheck(EnvSet e, ASTType target) throws TypeCheckError {
		ASTType tp = pair.typecheck(e, null);
        if (tp instanceof ASTTPair tpair)
            return choice > 0 ? tpair.getSecond().inst(tpair.getId(), new ASTChoice(pair, 0).normalize(e.getSigma())) : tpair.getFirst();
        else throw new TypeCheckError(ErrorMessages.illegalTypeToUnary(choice > 0 ? "snd" : "fst", tp));
	}
    
    public ASTNode weaknorm(Env<ASTNode> sub) {
        ASTNode pn = pair.weaknorm(sub);
        ASTNode first, second;
        if (pn instanceof ASTPair p) { first = p.getFirst(); second = p.getSecond(); }
        else return new ASTChoice(pn, choice);
        return choice == 0 ? first.weaknorm(sub) : second.weaknorm(sub);
    }

    public TermClosure solve(Env<ASTType> sigma) {
        TermClosure npair = pair.solve(sigma);
        if (npair == null) return null;
        Env<ASTType> sig = sigma;
        if (npair.term() instanceof ASTPair p) sig = p.getSig();
        return new TermClosure(new ASTChoice(npair.term(), choice), sig);
    }

    public ASTNode subs(String subsId, ASTNode node) {
        return new ASTChoice(pair.subs(subsId, node), choice);
    }

    @Override
    public String toString() {
        String s = choice == 0 ? "fst %s" : "snd %s";
        return String.format(s, pair);
    }
}
