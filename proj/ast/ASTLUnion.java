package proj.ast;

import proj.values.*;
import proj.types.*;
import proj.env.*;
import proj.errors.*;

import java.util.HashMap;

public class ASTLUnion extends ASTNode {
	private final String label;
	private final ASTNode expr;

	public ASTLUnion(String l, ASTNode e) {
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
		return new VUnion(label, expr.eval(e), true);
    }

    public ASTType typecheck(EnvSet e, ASTType target) throws TypeCheckError {
		HashMap<String, ASTType> ll = new HashMap<String, ASTType>();
		expr.setSig(e.getSigma());
		ll.put(label, expr.typecheck(e, null));
		return new ASTTLUnion(ll);
	}

	public ASTType puretypecheck(Env<ASTType> sigma, Env<ASTType> phi, AlphaEnv alpha, ASTType target) throws TypeCheckError {
		HashMap<String, ASTType> ll = new HashMap<String, ASTType>();
		expr.setSig(sigma);
		ll.put(label, expr.puretypecheck(sigma, phi, alpha, null));
		return new ASTTLUnion(ll);
	}

	public ASTNode weaknorm(Env<ASTNode> sub) {
		return new ASTLUnion(label, expr.weaknorm(sub));
    }

	public ASTNode subs(String subsId, ASTNode node) {
		return new ASTLUnion(label, expr.subs(subsId, node));
	}

	@Override
	public String toString() {
		return String.format("%s(%s)", label, expr);
	}
}
