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

    public boolean isSubtypeOf(ASTType o, Env<ASTType> sigma, Env<ASTType> phi, AlphaEnv alpha) {
        if (!(o instanceof ASTTId)) return phi.unfold(this).isSubtypeOf(o, sigma, phi, alpha);
        DefEq eq = new DefEq(sigma);
        return eq.typedefeq(this, o, sigma, phi, alpha);
    }

    public ASTType check(Env<ASTType> sigma, Env<ASTType> phi, AlphaEnv alpha) throws TypeCheckError {
        if (phi.find(id) == null) throw new TypeCheckError(ErrorMessages.idNotFound(id));
        return this;
    }
}	
