package proj.src.ast;

import proj.src.values.*;
import proj.src.types.*;
import proj.src.env.*;
import proj.src.errors.*;

public class ASTId extends ASTNode	{	
    private final String id;
    private String bid;
    
    public ASTId(String i)	{
        id = i;
    }

    public ASTId(String i, String b) {
        id = i; bid = b;
    }

    public String getId() { return id; }

    public String getBid() { return bid; }

    public IValue eval(Env<IValue> env) {
        return env.find(id);
    }

    public ASTType typecheck(EnvSet e, ASTType target) throws TypeCheckError {
        if (bid == null) bid = e.findBinderId(id);
        ASTType ret = e.findVar(id);
        return ret;
    }

    public ASTType puretypecheck(PureEnvSet pe, ASTType target) throws TypeCheckError {
        ASTType ret = pe.findVar(id);
        if (ret == null) throw new TypeCheckError(ErrorMessages.idNotFound(id));
        if (bid == null) bid = pe.findBinderId(id);
        return ret;
    }

    public ASTNode weaknorm(Env<ASTNode> sub) {
        ASTNode n = sub.find(id);
        return (n != null) ? n : this;
    }

    public ASTNode solve(Env<ASTType> sigma) {
        ASTNode n = sigma.findEq(bid);
        return n;
    }

    public ASTNode subs(String subsId, ASTNode node) {
        if (bid.equals(subsId)) return node;
        return this;
    }

    @Override
    public String toString() {
        return id;
    }
}	
