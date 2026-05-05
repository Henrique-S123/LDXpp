package proj.ast;

import proj.values.*;
import proj.types.*;
import proj.env.*;
import proj.errors.*;

public class ASTChoice implements ASTNode  {
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
            throw new InterpreterError((choice > 0 ? "snd" : "fst") + ": pair expected, found " + vp);
        }           
    }

    public ASTType typecheck(EnvSet e) throws TypeCheckError, EnvironmentError {
		ASTType tp = pair.typecheck(e);
        if (tp instanceof ASTTPair tpair) {
            return choice > 0 ? tpair.getSecond() : tpair.getFirst();
        } else {
            throw new TypeCheckError("illegal type to " + (choice > 0 ? "snd" : "fst") + ": " + tp);
        }
	}

    public ASTType typecheck(EnvSet e, ASTType t) throws TypeCheckError, EnvironmentError {
        return typecheck(e);
    }

    public ASTNode normalize(Env<ASTType> sigma, Env<ASTNode> sub) {
        ASTNode pn = pair.normalize(sigma, sub);
        ASTNode first, second;
        if (pn instanceof ASTPair p) { first = p.getFirst(); second = p.getSecond(); }
        else return new ASTChoice(pn, choice);
        return choice == 0 ? first.normalize(sigma, sub) : second.normalize(sigma, sub);
    }

    public boolean defequals(ASTNode o, Env<ASTType> sigma) {
        return o instanceof ASTChoice ochoice && ochoice.getChoice() == choice &&
            ochoice.getPair().defequals(pair, sigma);
    }

    @Override
    public String toString() {
        String s = choice == 0 ? "fst %s" : "snd %s";
        return String.format(s, pair);
    }
}
