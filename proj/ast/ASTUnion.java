package proj.ast;

import proj.values.*;
import proj.types.*;
import proj.env.*;
import proj.errors.*;

import java.util.HashMap;

public class ASTUnion extends ASTNode {
	String label;
	ASTNode expr;

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

    public ASTType typeinfer(EnvSet e) throws TypeCheckError, EnvironmentError {
		HashMap<String, ASTType> ll = new HashMap<String, ASTType>();
		Env<LinearBinding> prevDelta = e.popDelta();
		ASTType t = expr.typeinfer(e);
		e.pushDelta(prevDelta);
		ll.put(label, t);
		return new ASTTUnion(ll);
	}

	public ASTNode weaknorm(Env<ASTNode> sub) {
		return new ASTUnion(label, expr.weaknorm(sub));
    }

	public ASTNode subs(String subsId, ASTNode node) {
		return new ASTUnion(subsId, expr.subs(subsId, node));
	}

	@Override
	public String toString() {
		return String.format("%s(%s)", label, expr);
	}
}
