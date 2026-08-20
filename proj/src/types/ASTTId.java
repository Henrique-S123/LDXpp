package proj.src.types;

import proj.src.defeq.DefEq;
import proj.src.env.*;
import proj.src.errors.*;

public	class ASTTId extends ASTType	{	
    String id;	
    
    public ASTTId(String id) {
        this.id = id;
        lin = false;
    }

    public String getId() {
        return id;
    }

    public String toString() {
        return id;
    }

    public boolean isSubtypeOf(ASTType o, PureEnvSet pe, AlphaEnv alpha) {
        if (!(o instanceof ASTTId)) return pe.unfold(this).isSubtypeOf(o, pe, alpha);
        DefEq eq = new DefEq(pe.getSigma());
        return eq.typedefeq(this, o, pe.getPhi(), alpha);
    }

    public ASTType check(PureEnvSet pe) throws TypeCheckError {
        if (pe.findAlias(id) == null) throw new TypeCheckError(ErrorMessages.idNotFound(id));
        return this;
    }
}	
