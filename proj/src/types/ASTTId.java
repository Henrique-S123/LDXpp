package proj.src.types;

import proj.src.env.*;
import proj.src.errors.*;
import proj.src.defeq.IdPair;

import java.util.Set;

public	class ASTTId extends ASTType	{	
    private final String id;	
    
    public ASTTId(String i) {
        id = i; lin = false;
    }

    public String getId() { return id; }

    public String toString() { return id; }

    public boolean isSubtypeOf(ASTType o, PureEnvSet pe, AlphaEnv alpha, Set<IdPair> seen) {
        if (!(o instanceof ASTTId oid)) return pe.unfold(this).isSubtypeOf(o, pe, alpha, seen);
        IdPair p = new IdPair(id, oid.getId());
        if (seen.contains(p)) return true;
        seen.add(p);
        return pe.unfold(this).isSubtypeOf(pe.unfold(o), pe, alpha, seen);
    }

    public ASTType check(PureEnvSet pe) throws TypeCheckError {
        if (pe.findAlias(id) == null) throw new TypeCheckError(ErrorMessages.idNotFound(id));
        return this;
    }

    public boolean structEq(ASTType o) {
        return o instanceof ASTTId ot && id.equals(ot.getId());
    }
}	
