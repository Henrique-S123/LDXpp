package proj.types;

import proj.env.*;

public	class ASTTId extends ASTType	{	
    String id;	
    
    public ASTTId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String toString() {
        return id;
    }

    public boolean isSubtypeOf(ASTType o, Env<ASTType> phi, AlphaEnv alpha) {
        if (o instanceof ASTTId) return id.equals(((ASTTId) o).getId());
        return phi.unfold(this).isSubtypeOf(o, phi, alpha);
    }

    public boolean defequals(Env<ASTType> sl, ASTType o, Env<ASTType> sr, AlphaEnv alpha) {
        return o instanceof ASTTId oid && id.equals(oid.getId());
    }
}	
