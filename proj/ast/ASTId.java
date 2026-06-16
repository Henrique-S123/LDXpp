package proj.ast;

import proj.values.*;
import proj.types.*;
import proj.defeq.TermClosure;
import proj.env.*;
import proj.errors.*;

public class ASTId extends ASTNode	{	
    String id;	
    
    public ASTId(String id)	{
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public IValue eval(Env<IValue> env) {
        return env.find(id);
    }

    public ASTType typecheck(EnvSet e, ASTType target) throws TypeCheckError {
        return e.findVar(id);
    }

    public ASTNode weaknorm(Env<ASTNode> sub) {
        ASTNode n = sub.find(id);
        return (n != null) ? n : this;
    }

    public TermClosure solve(Env<ASTType> sigma) {
        ASTNode n = sigma.findEq(id);
        return n == null ? null : new TermClosure(n, sigma);
    }

    public ASTNode subs(String subsId, ASTNode node) {
        if (id.equals(subsId)) return node;
        return this;
    }

    @Override
    public String toString() {
        return id;
    }
}	
