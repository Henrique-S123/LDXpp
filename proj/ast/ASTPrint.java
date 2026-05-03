package proj.ast;

import proj.values.*;
import proj.types.*;
import proj.env.*;
import proj.errors.*;

public class ASTPrint implements ASTNode {
    ASTNode exp;
	boolean newline;

	public ASTPrint(ASTNode e, boolean nl) {
		exp = e;
		newline = nl;
    }

	public ASTNode getExp() {
		return exp;
	}

	public boolean getNewline() {
		return newline;
	}

    public IValue eval(Environment <IValue>e) throws InterpreterError {
		IValue v0 = exp.eval(e);
		String toprint = v0 instanceof VString ? v0.toString().substring(1, v0.toString().length()-1) : v0.toString();
		System.out.print(toprint + (newline ? "\n" : ""));
		return v0;
    }

	public ASTType typecheck(EnvSet e) throws TypeCheckError, EnvironmentError {
		exp.typecheck(e);
		return new ASTTUnit();
	}

	public ASTType typecheck(EnvSet e, ASTType t) throws TypeCheckError, EnvironmentError {
        return typecheck(e);
    }

	public ASTNode normalize(Environment<ASTType> sigma, Environment<ASTNode> e) {
		return new ASTPrint(exp.normalize(sigma, e), newline);
    }

	public boolean defequals(ASTNode o, Environment<ASTType> sigma) {
		return o instanceof ASTPrint oprint && oprint.getExp().defequals(exp, sigma)
			&& oprint.getNewline() == newline;
    }

	@Override
	public String toString() {
		return String.format("print%s(%s)", newline ? "ln" : "", exp);
	}
}
