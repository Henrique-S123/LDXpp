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

    public String getId() {
        return id;
    }

    public IValue eval(Environment<IValue> env)	throws InterpreterError {
        return env.find(id, false);
    }

    public ASTType typecheck(EnvSet e) throws TypeCheckError, EnvironmentError {
		return e.findVar(id);
	}

    public ASTNode normalize(Environment<ASTType> sigma) {
        ASTType e = sigma.findEq(this);
        if (e == null) return this;
        ASTTEq eq = ((ASTTEq) e);
        return eq.getTerm2().normalize(sigma);
    }

    public boolean equals(Object o) {
        return o instanceof ASTId && ((ASTId) o).getId().equals(id);
    }

    public String toString() {
        return id;
    }
}	
