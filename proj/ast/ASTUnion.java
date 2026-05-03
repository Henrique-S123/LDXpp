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

    public IValue eval(Environment<IValue> e) throws InterpreterError {
		return new VUnion(label, expr.eval(e), false);
    }

    public ASTType typecheck(EnvSet e) throws TypeCheckError, EnvironmentError {
		HashMap<String, ASTType> ll = new HashMap<String, ASTType>();
		Environment<ASTType> prevDelta = e.popDelta();
		ASTType t = expr.typecheck(e);
		e.setEnv(ENV.DELTA, prevDelta);
		ll.put(label, t);
		return new ASTTUnion(ll);
	}

	public ASTType typecheck(EnvSet e, ASTType t) throws TypeCheckError, EnvironmentError {
        return typecheck(e);
    }

	public ASTNode normalize(Environment<ASTType> sigma, Environment<ASTNode> e) {
		return new ASTUnion(label, expr.normalize(sigma, e));
    }

	public boolean defequals(ASTNode o, Environment<ASTType> sigma) {
		return o instanceof ASTUnion ounion && ounion.getLabel().equals(label)
			&& ounion.getExpr().defequals(expr, sigma);
    }

	@Override
	public String toString() {
		return String.format("%s(%s)", label, expr);
	}
}
