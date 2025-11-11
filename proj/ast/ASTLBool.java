package proj.ast;

import proj.values.*;
import proj.types.*;
import proj.env.*;
import proj.errors.*;

public class ASTLBool implements ASTNode  {
    boolean b;

    public ASTLBool(boolean b0) {
        b = b0;
    }

    public IValue eval(Environment<IValue> e) throws InterpreterError {
        return new VBool(b, true);                
    }

    public ASTType typecheck(Environment<ASTType> e) throws TypeCheckError {
		return new ASTTLBool();
	}

}
