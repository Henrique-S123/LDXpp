package proj.ast;

import proj.values.*;
import proj.types.*;
import proj.env.*;
import proj.errors.*;

public class ASTLInt implements ASTNode  {
    int v;

    public ASTLInt(int v0) {
        v = v0;
    }

    public IValue eval(Environment<IValue> e) throws InterpreterError {
        return new VInt(v, true);
    }

    public ASTType typecheck(Environment<ASTType> e) throws TypeCheckError {;
		return new ASTTLInt();
	}

}
