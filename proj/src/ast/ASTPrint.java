package proj.src.ast;

import proj.src.values.*;
import proj.src.types.*;
import proj.src.env.*;
import proj.src.errors.*;

public class ASTPrint extends ASTNode {
    private final ASTNode exp;
	private final boolean newline;

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

	public ASTType typecheck(EnvSet e, ASTType target) throws TypeCheckError {
		exp.typecheck(e, null);
		return new ASTTUnit();
	}

	public ASTType puretypecheck(Env<ASTType> sigma, Env<ASTType> phi, AlphaEnv alpha, ASTType target) throws TypeCheckError {
		exp.puretypecheck(sigma, phi, alpha, null);
		return new ASTTUnit();
	}

	public ASTNode weaknorm(Env<ASTNode> sub) {
		return new ASTPrint(exp.weaknorm(sub), newline);
    }

	public ASTNode subs(String subsId, ASTNode node) {
		return new ASTPrint(exp.subs(subsId, node), newline);
	}

	@Override
	public String toString() {
		return String.format("print%s(%s)", newline ? "ln" : "", exp);
	}
}
