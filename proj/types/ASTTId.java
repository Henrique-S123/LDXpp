package proj.types;

import proj.env.*;
import proj.errors.*;

public	class ASTTId implements ASTType	{	

    String id;	
    
    public ASTTId(String id)	{
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String toStr() {
        return id;
    }

    public boolean isSubtypeOf(ASTType o, Environment<ASTType> e) throws InterpreterError {
        if (o instanceof ASTTId) {
            return id.equals(((ASTTId) o).getId());
        }
        return e.unfold(this).isSubtypeOf(o, e);
    }
}	
