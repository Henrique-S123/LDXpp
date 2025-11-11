package proj.ast;

import proj.values.*;
import proj.types.*;
import proj.env.*;
import proj.errors.*;

import java.util.HashMap;

public class ASTStruct implements ASTNode {

    ExprBindList ell;

	public ASTStruct(ExprBindList l) {
		ell = l;
    }

    public IValue eval(Environment<IValue> e) throws InterpreterError {
		HashMap<String, IValue> vll = new HashMap<String, IValue>();
		for (String k : ell.getMap().keySet()) {
			vll.put(k, ell.getMap().get(k).eval(e));
		}
		return new VStruct(vll);
    }

    public ASTType typecheck(Environment<ASTType> e) throws TypeCheckError, InterpreterError {
		HashMap<String, ASTType> typeslist = new HashMap<String, ASTType>();
		for (String k : ell.getMap().keySet()) {
			typeslist.put(k, ell.getMap().get(k).typecheck(e));
		}
		return new ASTTStruct(new TypeBindList(typeslist));
	}

}
