package proj.types;

import proj.env.*;
import proj.errors.*;

public class ASTTList implements ASTType {
    private ASTType elt;

    public ASTTList(ASTType eltt)
    {
        elt = eltt;
    }
    
    public String toStr() {
        return "list<"+elt.toStr()+">";
    }

    public ASTType getType() {
        return elt;
    }

    public void setType(ASTType t) {
        elt = t;
    }

    public boolean isSubtypeOf(ASTType o, Environment<ASTType> e) throws InterpreterError {
        if (o instanceof ASTTId) {
            ASTType to = e.unfold(o);
            return this.isSubtypeOf(to, e);
        } else if (o instanceof ASTTList) {
            ASTType tlist = ((ASTTList) o).getType();
            return elt.isSubtypeOf(tlist, e);
        }
        return false;
    }
}
