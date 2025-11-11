package proj.ast;

import proj.values.*;
import proj.types.*;
import proj.env.*;
import proj.errors.*;

public class ASTPrint implements ASTNode {
    ASTNode exp;
	boolean newline;

    public IValue eval(Environment <IValue>e) throws InterpreterError {
		IValue v0 = exp.eval(e);
		String toprint;
		if (v0 instanceof VString) {
			toprint = v0.toStr().substring(1, v0.toStr().length()-1);
		} else {
			toprint = v0.toStr();
		}
		System.out.print(toprint + (newline ? "\n" : ""));
		return v0;
    }
        
    public ASTPrint(ASTNode e, boolean nl) {
		exp = e;
		newline = nl;
    }

	public ASTType typecheck(Environment<ASTType> e) throws TypeCheckError, InterpreterError {
		return exp.typecheck(e);
	}	
}
