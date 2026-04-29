package proj.types;

import proj.env.*;

public	class ASTTId implements ASTType	{	
    String id;	
    
    public ASTTId(String id)	{
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String toString() {
        return id;
    }

    public boolean isSubtypeOf(ASTType o, EnvSet e) {
        if (o instanceof ASTTId) {
            return id.equals(((ASTTId) o).getId());
        }
        return e.unfold(this).isSubtypeOf(o, e);
    }

    public boolean defequals(ASTType o, Environment<ASTType> sigma) {
        return o instanceof ASTTId oid && oid.getId().equals(id);
    }
}	
