package proj.types;

import proj.ast.*;
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

    public boolean isSubtypeOf(ASTType o, EnvSet e) {
        if (o instanceof ASTTId) return id.equals(((ASTTId) o).getId());
        return e.unfold(this).isSubtypeOf(o, e);
    }

    public boolean defequals(ASTType o, Environment<ASTType> sigma, Environment<ASTNode> alphaL, Environment<ASTNode> alphaR) {
        if (!(o instanceof ASTTId oid)) return false;
        ASTNode f1 = alphaL.find(id, false);
        ASTNode f2 = alphaR.find(oid.getId(), false);
        if (f1 != null && f2 != null && f1 instanceof ASTId nid1 && f2 instanceof ASTId nid2) 
            return nid1.getId().equals(nid2.getId());
        return id.equals(oid.getId());
    }
}	
