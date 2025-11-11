package proj.types;

import proj.env.*;
import proj.errors.*;

public class ASTTArrow implements ASTType {
    ASTType dom;
    ASTType codom;

    public ASTTArrow(ASTType d, ASTType co) {
        dom = d;
        codom = co;
    }

    public ASTType getDom() {
        return dom;
    }

    public ASTType getCodom() {
        return codom;
    }

    public String toStr() {
        return dom.toStr()+"->"+codom.toStr();
    }

    public boolean isSubtypeOf(ASTType o, Environment<ASTType> e) throws InterpreterError {
        if (o instanceof ASTTId) {
            ASTType to = e.unfold(o);
            return this.isSubtypeOf(to, e);
        } else if (o instanceof ASTTArrow) {
            ASTType odom = ((ASTTArrow) o).getDom();
            ASTType ocodom = ((ASTTArrow) o).getCodom();
            return odom.isSubtypeOf(dom, e) && codom.isSubtypeOf(ocodom, e);
        }
        return false;
    }
}

