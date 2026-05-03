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

    public ASTNode normalize(Environment<ASTType> sigma, Environment<ASTNode> e) {
        // TODO: verify sigma and e dont have ids in common
        ASTNode n = e.find(id, false);
        if (n != null) return n;

        ASTType eq = sigma.findEq(this);
        return eq == null ? this : ((ASTTEq) eq).getTerm2().normalize(sigma, e);
    }

    public boolean defequals(ASTNode o, Environment<ASTType> sigma) {
        return (o instanceof ASTId oid && oid.getId().equals(id));
    }

    @Override
    public String toString() {
        return id;
    }
}	
