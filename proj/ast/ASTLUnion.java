package proj.ast;

import proj.values.*;
import proj.types.*;
import proj.env.*;
import proj.errors.*;

import java.util.HashMap;

public class ASTLUnion extends ASTNode {
	String label;
	ASTNode expr;

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

    public ASTType typecheck(EnvSet e) throws TypeCheckError, EnvironmentError {
		HashMap<String, ASTType> ll = new HashMap<String, ASTType>();
		ll.put(label, expr.typecheck(e));
		return new ASTTLUnion(ll);
	}

	public ASTNode weaknorm(Env<ASTType> sigma, Env<ASTNode> sub) {
		return new ASTLUnion(label, expr.weaknorm(sigma, sub));
    }

	public ASTNode solve(Env<ASTType> sigma) {
		ASTNode nexpr = expr.solve(sigma);
		return (nexpr == null) ? null : new ASTLUnion(label, nexpr);
    }

	public ASTNode subs(String subsId, ASTNode node) {
		return new ASTLUnion(subsId, expr.subs(subsId, node));
	}

	public boolean defequals(ASTNode o, Env<ASTType> sigma, AlphaEnv alpha) {
		return o instanceof ASTLUnion olunion && olunion.getLabel().equals(label)
			&& olunion.getExpr().defequals(expr, sigma, alpha);
    }

	@Override
	public String toString() {
		return String.format("%s(%s)", label, expr);
	}
}
