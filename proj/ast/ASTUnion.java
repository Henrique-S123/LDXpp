package proj.ast;

import proj.values.*;
import proj.types.*;
import proj.env.*;
import proj.errors.*;

import java.util.HashMap;

public class ASTUnion extends ASTNode {
	private final String label;
	private final ASTNode expr;

	public ASTUnion(String l, ASTNode e) {
		label = l;
		expr = e;
    }

	public String getLabel() {
		return label;
	}

	public ASTNode getExpr() {
		return expr;
	}

    public IValue eval(Env<IValue> e) throws InterpreterError {
		return new VUnion(label, expr.eval(e), false);
    }

    public ASTType typecheck(EnvSet e, ASTType target) throws TypeCheckError {
		HashMap<String, ASTType> ll = new HashMap<String, ASTType>();
		Env<LinearBinding> prevDelta = e.popDelta();
		expr.setSig(e.getSigma());
		ll.put(label, expr.typecheck(e, null));
		e.pushDelta(prevDelta);
		return new ASTTUnion(ll);
	}

	public ASTType puretypecheck(Env<ASTType> sigma, Env<ASTType> phi, AlphaEnv alpha, ASTType target) throws TypeCheckError {
		HashMap<String, ASTType> ll = new HashMap<String, ASTType>();
		expr.setSig(sigma);
		ll.put(label, expr.puretypecheck(sigma, phi, alpha, null));
		return new ASTTUnion(ll);
	}

	public ASTNode weaknorm(Env<ASTNode> sub) {
		return new ASTUnion(label, expr.weaknorm(sub));
    }

	public ASTNode subs(String subsId, ASTNode node) {
		return new ASTUnion(label, expr.subs(subsId, node));
	}

	@Override
	public String toString() {
		return String.format("%s(%s)", label, expr);
	}
}
