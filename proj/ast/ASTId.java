package proj.ast;

import proj.values.*;
import proj.types.*;
import proj.env.*;
import proj.errors.*;

public class ASTId extends ASTNode	{	
    private final String id;
    private String binderId;
    
    public ASTId(String id)	{
        this.id = id;
    }

    public ASTId(String id, String bid) {
        this.id = id;
        binderId = bid;
    }

    public String getId() {
        return id;
    }

    public String getBinderId() {
        return binderId;
    }

    public void setBinderId(String id) {
        binderId = id;
    }

    public IValue eval(Env<IValue> env) {
        return env.find(id);
    }

    public ASTType typecheck(EnvSet e, ASTType target) throws TypeCheckError {
        ASTType ret = e.findVar(id);
        if (binderId == null) binderId = e.findBinderId(id);
        return ret;
    }

    public ASTType puretypecheck(Env<ASTType> sigma, Env<ASTType> phi, ASTType target) throws TypeCheckError {
        ASTType ret = sigma.find(id);
        if (ret == null) throw new TypeCheckError(ErrorMessages.idNotFound(id));
        if (binderId == null) binderId = sigma.findBinderId(id);
        return ret;
    }

    public ASTNode weaknorm(Env<ASTNode> sub) {
        ASTNode n = sub.find(id);
        return (n != null) ? n : this;
    }

    public ASTNode solve(Env<ASTType> sigma) {
        ASTNode n = sigma.findEq(id);
        return n;
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
