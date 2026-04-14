package proj.types;

import proj.env.*;
import proj.errors.*;

public class ASTTLollipop implements ASTLinType {
    ASTType dom;
    ASTType codom;

    public ASTTLollipop(ASTType d, ASTType co) {
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
        return dom.toStr()+"-o"+codom.toStr();
    }

    public boolean isSubtypeOf(ASTType o, EnvSet e) throws InterpreterError {
        if (o instanceof ASTTId) {
            ASTType to = e.unfold(o);
            return this.isSubtypeOf(to, e);
        } else if (o instanceof ASTTLollipop) {
            ASTType odom = ((ASTTLollipop) o).getDom();
            ASTType ocodom = ((ASTTLollipop) o).getCodom();
            return odom.isSubtypeOf(dom, e) && codom.isSubtypeOf(ocodom, e);
        }
        return false;
    }

    public boolean defequals(ASTType o, Environment<ASTType> sigma) {
        return o instanceof ASTTLollipop && ((ASTTLollipop) o).getDom().defequals(dom, sigma)
            && ((ASTTLollipop) o).getCodom().defequals(codom, sigma);
    }
}

