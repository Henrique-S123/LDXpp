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

    public IValue eval(Environment<IValue> env) {
        return env.find(id, false);
    }

    public ASTType typecheck(EnvSet e) throws EnvironmentError {
		return e.findVar(id);
	}

    public ASTType typecheck(EnvSet e, ASTType t) throws TypeCheckError, EnvironmentError {
        return typecheck(e);
    }

    public ASTNode normalize(Environment<ASTType> sigma) {
        ASTType e = sigma.findEq(this);
        if (e == null) return this;
        ASTTEq eq = ((ASTTEq) e);
        return eq.getTerm2().normalize(sigma);
    }

    public boolean defequals(ASTNode o, Environment<ASTType> sigma) {
        // TODO
        if (o instanceof ASTId && ((ASTId) o).getId().equals(id)) return true;
        return this.normalize(sigma).defequals(o, sigma);
    }

    public String toString() {
        return id;
    }
}	
