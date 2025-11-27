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

    public IValue eval(Environment<IValue> e) throws InterpreterError {
		return new VUnion(label, expr.eval(e), true);
    }

    public ASTType typecheck(EnvSet e) throws TypeCheckError, InterpreterError {
		HashMap<String, ASTType> ll = new HashMap<String, ASTType>();
		ll.put(label, expr.typecheck(e));
		return new ASTTLUnion(new TypeBindList(ll));
	}
}
