package proj.ast;

import proj.values.*;
import proj.types.*;
import proj.env.*;
import proj.errors.*;

public class ASTPrint extends ASTNode {
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

    public IValue eval(Env <IValue>e) throws InterpreterError {
		IValue v0 = exp.eval(e);
		String toprint = v0 instanceof VString ? v0.toString().substring(1, v0.toString().length()-1) : v0.toString();
		System.out.print(toprint + (newline ? "\n" : ""));
		return v0;
    }

	public ASTType typecheck(EnvSet e) throws TypeCheckError, EnvironmentError {
		exp.typecheck(e);
		return new ASTTUnit();
	}

	public ASTNode weaknorm(Env<ASTNode> sub) {
		return new ASTPrint(exp.weaknorm(sub), newline);
    }

	public ASTNode solve(Env<ASTType> sigma) {
		ASTNode nexp = exp.solve(sigma);
		return (nexp == null) ? null : new ASTPrint(nexp, newline);
    }

	public ASTNode subs(String subsId, ASTNode node) {
		return new ASTPrint(exp.subs(subsId, node), newline);
	}

	@Override
	public String toString() {
		return String.format("print%s(%s)", newline ? "ln" : "", exp);
	}
}
