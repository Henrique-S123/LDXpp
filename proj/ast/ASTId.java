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

    public IValue eval(Env<IValue> env) {
        return env.find(id, false);
    }

    public ASTType typecheck(EnvSet e) throws EnvironmentError {
		return e.findVar(id);
	}

    public ASTType typecheck(EnvSet e, ASTType t) throws TypeCheckError, EnvironmentError {
        return typecheck(e);
    }

    public ASTNode normalize(Env<ASTType> sigma, Env<ASTNode> sub) {
        // TODO: verify sigma and sub dont have ids in common
        ASTNode n = sub.find(id, false);
        if (n != null) return n;
        ASTType eq = sigma.findEq(this);
        return eq == null ? this : ((ASTTEq) eq).getTerm2().normalize(sigma, sub);
    }

    public boolean defequals(ASTNode o, Env<ASTType> sigma) {
        return (o instanceof ASTId oid && oid.getId().equals(id));
    }

    @Override
    public String toString() {
        return id;
    }
}	
