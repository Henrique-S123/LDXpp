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

    public IValue eval(Environment<IValue> e) throws InterpreterError {
        IValue vp = pair.eval(e);
        if (vp instanceof VPair pair) {
            return choice > 0 ? pair.getSecond() : pair.getFirst();
        } else {
            throw new InterpreterError((choice > 0 ? "snd" : "fst") + ": pair expected, found " + vp.toStr());
        }           
    }

    public ASTType typecheck(EnvSet e) throws TypeCheckError, EnvironmentError {
		ASTType tp = pair.typecheck(e);
        if (tp instanceof ASTTPair tpair) {
            return choice > 0 ? tpair.getSecond() : tpair.getFirst();
        } else {
            throw new TypeCheckError("illegal type to " + (choice > 0 ? "snd" : "fst") + ": " + tp.toStr());
        }
	}

    public ASTType typecheck(EnvSet e, ASTType t) throws TypeCheckError, EnvironmentError {
        return typecheck(e);
    }

    public ASTNode normalize(Environment<ASTType> sigma) {
        return choice == 0 ?
            ((ASTPair) pair.normalize(sigma)).getFirst().normalize(sigma):
            ((ASTPair) pair.normalize(sigma)).getSecond().normalize(sigma);
    }

    public boolean defequals(ASTNode o, Environment<ASTType> sigma) {
        return o instanceof ASTChoice ochoice && ochoice.getChoice() == choice &&
            ochoice.getPair().defequals(pair, sigma);
    }

    @Override
    public String toString() {
        String s = choice == 0 ? "fst %s" : "snd %s";
        return String.format(s, pair.toString());
    }
}
