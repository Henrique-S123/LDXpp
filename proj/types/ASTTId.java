package proj.types;

import proj.ast.ASTNode;
import proj.env.*;

public	class ASTTId implements ASTType	{	
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

    public boolean isSubtypeOf(ASTType o, EnvSet e, AlphaEnv alpha) {
        if (o instanceof ASTTId) return id.equals(((ASTTId) o).getId());
        return e.unfold(this).isSubtypeOf(o, e, alpha);
    }

    public boolean defequals(Env<ASTType> sl, ASTType o, Env<ASTType> sr, AlphaEnv alpha) {
        return o instanceof ASTTId oid && id.equals(oid.getId());
    }

    public ASTType inst(String instId, ASTNode n) {
        return this;
    }
}	
