package proj.src.ast;

import proj.src.values.*;
import proj.src.types.*;
import proj.src.env.*;
import proj.src.errors.*;

import java.util.HashMap;

public class ASTUnion extends ASTNode {
	private final String label;
	private final ASTNode expr;
	private final boolean lin;

	public ASTUnion(String l, ASTNode e, boolean li) {
		label = l; expr = e; lin = li;
    }

	public String getLabel() { return label; }

	public ASTNode getExpr() { return expr; }

	public boolean isLinear() { return lin; }

    public IValue eval(Env<IValue> e) throws InterpreterError {
		return new VUnion(label, expr.eval(e), lin);
    }

    public ASTType typecheck(EnvSet e, ASTType target) throws TypeCheckError {
		HashMap<String, ASTType> ll = new HashMap<String, ASTType>();
		expr.setSig(e.getSigma());
		ResourceManager<ASTType> prevDelta = null;
		if (!lin) prevDelta = e.popDelta();
		ll.put(label, expr.typecheck(e, null));
		if (!lin) e.pushDelta(prevDelta);
		return new ASTTUnion(ll, lin);
	}

	public ASTType puretypecheck(Env<ASTType> sigma, Env<ASTType> phi, AlphaEnv alpha, ASTType target) throws TypeCheckError {
		HashMap<String, ASTType> ll = new HashMap<String, ASTType>();
		expr.setSig(sigma);
		ll.put(label, expr.puretypecheck(sigma, phi, alpha, null));
		return new ASTTUnion(ll, lin);
	}

	public ASTNode weaknorm(Env<ASTNode> sub) {
		return new ASTUnion(label, expr.weaknorm(sub), lin);
    }

	public ASTNode subs(String subsId, ASTNode node) {
		return new ASTUnion(label, expr.subs(subsId, node), lin);
	}

	@Override
	public String toString() {
		return String.format("%s(%s)", label, expr);
	}
}
