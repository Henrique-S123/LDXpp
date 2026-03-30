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
        if (vp instanceof VPair) {
            return choice > 0 ? ((VPair) vp).getSecond() : ((VPair) vp).getFirst();
        } else {
            throw new InterpreterError((choice > 0 ? "snd" : "fst") + ": pair expected, found " + vp.toStr());
        }           
    }

    public ASTType typecheck(EnvSet e) throws TypeCheckError, InterpreterError {
		ASTType tp = pair.typecheck(e);
        if (tp instanceof ASTTPair) {
            return choice > 0 ? ((ASTTPair) tp).getSecond() : ((ASTTPair) tp).getFirst();
        } else {
            throw new TypeCheckError("illegal type to " + (choice > 0 ? "snd" : "fst") + ": " + tp.toStr());
        }
	}

    public ASTNode normalize() {
        return choice == 0 ?
            ((ASTPair) pair).getFirst().normalize():
            ((ASTPair) pair).getSecond().normalize();
    }

    @Override
    public String toString() {
        String s = choice == 0 ? "fst %s" : "snd %s";
        return String.format(s, pair.toString());
    }
}
