package proj.ast;

import proj.values.*;
import proj.types.*;
import proj.env.*;
import proj.errors.*;

public class ASTSeq implements ASTNode {

    ASTNode first, second;

    public IValue eval(Environment<IValue> e) throws InterpreterError {
		first.eval(e);
		return second.eval(e);
    }

    public ASTSeq(ASTNode f, ASTNode s) {
		first = f;
		second = s;
    }

    public ASTType typecheck(Environment<ASTType> e) throws TypeCheckError, InterpreterError {
    first.typecheck(e);
		return second.typecheck(e);
	}

}
