package proj.ast;

import proj.values.*;
import proj.types.*;
import proj.env.*;
import proj.errors.*;

public class ASTId implements ASTNode	{	
    String id;	
    
    public ASTId(String id)	{
        this.id = id;
    }

    public IValue eval(Environment<IValue> env)	throws InterpreterError {
        return env.find(id, false);
    }

    public ASTType typecheck(EnvSet e) throws TypeCheckError, InterpreterError {
		return e.findVar(id);
	}

}	
