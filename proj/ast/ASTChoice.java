package proj.ast;

import proj.values.*;
import proj.types.*;
import proj.env.*;
import proj.env.EnvSet.ENV;
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

    public ASTType typecheck(EnvSet e) throws TypeCheckError, EnvironmentError {
		ASTType tp = pair.typecheck(e);
        if (tp instanceof ASTTPair tpair) {
            return choice > 0 ? tpair.getSecond().inst(tpair.getId(), new ASTChoice(pair, 0).normalize(e.getEnv(ENV.SIGMA))) : tpair.getFirst();
        } else {
            throw new TypeCheckError(ErrorMessages.illegalTypeToUnary(choice > 0 ? "snd" : "fst", tp));
        }
	}
    
    public ASTNode weaknorm(Env<ASTNode> sub) {
        ASTNode pn = pair.weaknorm(sub);
        ASTNode first, second;
        if (pn instanceof ASTPair p) { first = p.getFirst(); second = p.getSecond(); }
        else return new ASTChoice(pn, choice);
        return choice == 0 ? first.weaknorm(sub) : second.weaknorm(sub);
    }

    public ASTNode solve(Env<ASTType> sigma) {
        ASTNode npair = pair.solve(sigma);
        return (npair == null) ? null : new ASTChoice(npair, choice);
    }

    public ASTNode subs(String subsId, ASTNode node) {
        return new ASTChoice(pair.subs(subsId, node), choice);
    }

    public boolean defequals(ASTNode o, Env<ASTType> sigma, AlphaEnv alpha) {
        return o instanceof ASTChoice ochoice && ochoice.getChoice() == choice &&
            ochoice.getPair().defequals(pair, sigma, alpha);
    }

    @Override
    public String toString() {
        String s = choice == 0 ? "fst %s" : "snd %s";
        return String.format(s, pair);
    }
}
