package proj.ast;

import proj.values.*;
import proj.types.*;
import proj.env.*;
import proj.errors.*;

public class ASTString implements ASTNode  {
    String s;

    public ASTString(String s0) {
        s = s0;
    }

    public IValue eval(Environment<IValue> e) throws InterpreterError {
        return new VString(s);                
    }

    public ASTType typecheck(Environment<ASTType> e) throws TypeCheckError {
		return new ASTTString();
	}

}
