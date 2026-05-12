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
        return (n != null) ? n : this;
    }

    public ASTNode solve(Env<ASTType> sigma) {
        ASTType eq = sigma.findEq(this);
        return (eq == null) ? null : ((ASTTEq) eq).getTerm2();
    }

    public ASTNode subs(String subsId, ASTNode node) {
        if (id.equals(subsId)) return node;
        return this;
    }

    public boolean defequals(ASTNode o, Env<ASTType> sigma, AlphaEnv alpha) {
        if (o instanceof ASTId oid) {
            String s1 = alpha.getLeft().find(id, false);
            String s2 = alpha.getRight().find(oid.getId(), false);
            if (s1 != null && s2 != null) return s1.equals(s2);
            return id.equals(oid.getId());
        }
        return false;
    }

    @Override
    public String toString() {
        return id;
    }
}	
