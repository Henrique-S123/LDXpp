package proj.ast;

import proj.values.*;
import proj.types.*;
import proj.env.*;
import proj.errors.*;

public class ASTNil implements ASTNode  {

    public ASTNil() {}

    public IValue eval(Environment<IValue> e) throws InterpreterError {
        return new VNil();                
    }

    public ASTType typecheck(Environment<ASTType> e) throws TypeCheckError {
		return new ASTTList(new ASTTUnit());
	}

}
