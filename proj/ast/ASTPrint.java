package proj.ast;

import proj.values.*;
import proj.types.*;
import proj.env.*;
import proj.errors.*;

public class ASTPrint implements ASTNode {
    ASTNode exp;
	boolean newline;

	public ASTPrint(ASTNode e, boolean nl) {
		exp = e;
		newline = nl;
    }

    public IValue eval(Environment <IValue>e) throws InterpreterError {
		IValue v0 = exp.eval(e);
		String toprint = v0 instanceof VString ? v0.toStr().substring(1, v0.toStr().length()-1) : v0.toStr();
		System.out.print(toprint + (newline ? "\n" : ""));
		return v0;
    }

	public ASTType typecheck(EnvSet e) throws TypeCheckError, InterpreterError {
		exp.typecheck(e);
		return new ASTTUnit();
	}	
}
