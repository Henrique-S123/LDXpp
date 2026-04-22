package proj.ast;

import proj.values.*;
import proj.types.*;
import proj.env.*;
import proj.errors.*;

import java.util.HashMap;

public class ASTLUnion implements ASTNode {

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

    public IValue eval(Environment<IValue> e) throws InterpreterError {
		return new VUnion(label, expr.eval(e), true);
    }

    public ASTType typecheck(EnvSet e) throws TypeCheckError, EnvironmentError {
		HashMap<String, ASTType> ll = new HashMap<String, ASTType>();
		ll.put(label, expr.typecheck(e));
		return new ASTTLUnion(ll);
	}

	public ASTType typecheck(EnvSet e, ASTType t) throws TypeCheckError, EnvironmentError {
        return typecheck(e);
    }

	public ASTNode normalize(Environment<ASTType> sigma) {
		return new ASTLUnion(label, expr.normalize(sigma));
    }

	public boolean defequals(ASTNode o, Environment<ASTType> sigma) {
		return o instanceof ASTLUnion && ((ASTLUnion) o).getLabel().equals(label)
			&& ((ASTLUnion) o).getExpr().defequals(expr, sigma);
    }
}
