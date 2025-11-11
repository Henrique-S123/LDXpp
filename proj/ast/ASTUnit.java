package proj.ast;

import proj.values.*;
import proj.types.*;
import proj.env.*;
import proj.errors.*;

public class ASTUnit implements ASTNode  {

    public ASTUnit() {}

    public IValue eval(Environment<IValue> e) throws InterpreterError {
        return new VUnit();                
    }

    public ASTType typecheck(Environment<ASTType> e) throws TypeCheckError {
		return new ASTTUnit();
	}

}
