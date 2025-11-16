package proj.ast;

import proj.values.*;
import proj.types.*;
import proj.env.*;
import proj.errors.*;

public class ASTLUnit implements ASTNode  {

    public ASTLUnit() {}

    public IValue eval(Environment<IValue> e) throws InterpreterError {
        return new VUnit(true);                
    }

    public ASTType typecheck(Environment<ASTType> e) throws TypeCheckError {
		return new ASTTLUnit();
	}
}
