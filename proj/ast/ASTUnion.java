package proj.ast;

import proj.values.*;
import proj.types.*;
import proj.env.*;
import proj.env.EnvSet.ENV;
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

    public ASTType typecheck(EnvSet e) throws TypeCheckError, EnvironmentError {
		HashMap<String, ASTType> ll = new HashMap<String, ASTType>();
		Env<ASTType> prevDelta = e.popDelta();
		ASTType t = expr.typecheck(e);
		e.setEnv(ENV.DELTA, prevDelta);
		ll.put(label, t);
		return new ASTTUnion(ll);
	}

	public ASTNode weaknorm(Env<ASTNode> sub) {
		return new ASTUnion(label, expr.weaknorm(sub));
    }

	public ASTNode solve(Env<ASTType> sigma) {
		ASTNode nexpr = expr.solve(sigma);
		return (nexpr == null) ? null : new ASTUnion(label, nexpr);
    }

	public ASTNode subs(String subsId, ASTNode node) {
		return new ASTUnion(subsId, expr.subs(subsId, node));
	}

	public boolean defequals(Env<ASTType> sl, ASTNode o, Env<ASTType> sr, AlphaEnv alpha) {
		return o instanceof ASTUnion ounion && label.equals(ounion.getLabel())
			&& expr.defequals(sl, ounion.getExpr(), sr, alpha);
    }

	@Override
	public String toString() {
		return String.format("%s(%s)", label, expr);
	}
}
