package proj.ast;

import proj.values.*;
import proj.types.*;
import proj.env.*;
import proj.env.EnvSet.ENV;
import proj.errors.*;

import java.util.HashMap;

public class ASTUnion implements ASTNode {
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

	public ASTType typecheck(EnvSet e, ASTType t) throws TypeCheckError, EnvironmentError {
        return typecheck(e);
    }

	public ASTNode normalize(Env<ASTType> sigma, Env<ASTNode> sub) {
		return new ASTUnion(label, expr.normalize(sigma, sub));
    }

	public boolean defequals(ASTNode o, Env<ASTType> sigma, AlphaEnv alpha) {
		return o instanceof ASTUnion ounion && ounion.getLabel().equals(label)
			&& ounion.getExpr().defequals(expr, sigma, alpha);
    }

	@Override
	public String toString() {
		return String.format("%s(%s)", label, expr);
	}
}
