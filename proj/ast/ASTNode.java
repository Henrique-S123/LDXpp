package proj.ast;

import proj.values.*;
import proj.types.ASTType;
import proj.env.*;
import proj.errors.*;

public interface ASTNode {

    public IValue eval(Environment<IValue> e) throws InterpreterError;
	
    public ASTType typecheck(Environment<ASTType> e) throws TypeCheckError, InterpreterError;

}

