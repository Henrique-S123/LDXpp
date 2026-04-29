package proj.ast;

import proj.values.*;
import proj.types.*;
import proj.env.*;
import proj.errors.*;

public class ASTSeq implements ASTNode {
    ASTNode first, second;

    public ASTSeq(ASTNode f, ASTNode s) {
		first = f;
		second = s;
    }

    public ASTNode getFirst() {
        return first;
    }

    public ASTNode getSecond() {
        return second;
    }

    public IValue eval(Environment<IValue> e) throws InterpreterError {
		first.eval(e);
		return second.eval(e);
    }

    public ASTType typecheck(EnvSet e) throws TypeCheckError, EnvironmentError {
        ASTType tf = first.typecheck(e);
        if (tf instanceof ASTTUnit) {
            return second.typecheck(e);
        } else {
            throw new TypeCheckError("illegal type to sequential composition: " + tf);
        }
	}

    public ASTType typecheck(EnvSet e, ASTType t) throws TypeCheckError, EnvironmentError {
        return typecheck(e);
    }

	public ASTNode normalize(Environment<ASTType> sigma) {
        return new ASTSeq(first.normalize(sigma), second.normalize(sigma));
    }

    public boolean defequals(ASTNode o, Environment<ASTType> sigma) {
        return o instanceof ASTSeq oseq && oseq.getFirst().defequals(first, sigma)
            && oseq.getSecond().defequals(second, sigma);
    }

    @Override
	public String toString() {
		return String.format("%s; %s", first, second);
	}
}
