package proj.ast;

import proj.values.*;
import proj.types.*;
import proj.env.*;
import proj.errors.*;

import java.util.HashMap;

public class ASTUnion implements ASTNode {

	String label;
	ASTNode expr;

	public ASTUnion(String l, ASTNode e) {
		label = l;
		expr = e;
    }

    public IValue eval(Environment<IValue> e) throws InterpreterError {
		return new VUnion(label, expr.eval(e));
    }

    public ASTType typecheck(Environment<ASTType> e) throws TypeCheckError, InterpreterError {
		HashMap<String, ASTType> ll = new HashMap<String, ASTType>();
		ll.put(label, expr.typecheck(e));
		return new ASTTUnion(new TypeBindList(ll));
	}
}
