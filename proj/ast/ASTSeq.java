package proj.ast;

import proj.values.*;
import proj.types.*;
import proj.env.*;
import proj.errors.*;

public class ASTSeq implements ASTNode {
    ASTNode first, second;

    public IValue eval(Environment<IValue> e) throws InterpreterError {
		first.eval(e);
		return second.eval(e);
    }

    public ASTSeq(ASTNode f, ASTNode s) {
		first = f;
		second = s;
    }

    public ASTType typecheck(EnvSet e) throws TypeCheckError, EnvironmentError {
        ASTType tf = first.typecheck(e);
        if (tf instanceof ASTTUnit) {
            return second.typecheck(e);
        } else {
            throw new TypeCheckError("illegal type to sequential composition: " + tf.toStr());
        }
	}

    public ASTType typecheck(EnvSet e, ASTType t) throws TypeCheckError, EnvironmentError {
        return typecheck(e);
    }

	public ASTNode normalize(Environment<ASTType> sigma) {
        return new ASTSeq(first.normalize(sigma), second.normalize(sigma));
    }

    public boolean defequals(ASTNode o, Environment<ASTType> sigma) {
        // TODO
        return false;
    }
}
