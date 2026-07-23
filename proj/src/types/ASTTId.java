package proj.src.types;

import proj.src.defeq.DefEq;
import proj.src.env.*;

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
}	
