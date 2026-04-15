package proj.types;

import proj.env.*;

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

    public boolean isSubtypeOf(ASTType o, EnvSet e) {
        if (o instanceof ASTTId) {
            ASTType to = e.unfold(o);
            return this.isSubtypeOf(to, e);
        } else if (o instanceof ASTTArrow) {
            ASTType odom = ((ASTTArrow) o).getDom();
            ASTType ocodom = ((ASTTArrow) o).getCodom();
            return odom.isSubtypeOf(dom, e) && codom.isSubtypeOf(ocodom, e);
        } else if (o instanceof ASTTLollipop) {
            ASTType odom = ((ASTTLollipop) o).getDom();
            ASTType ocodom = ((ASTTLollipop) o).getCodom();
            return odom.isSubtypeOf(dom, e) && codom.isSubtypeOf(ocodom, e);
        }
        return false;
    }

    public boolean defequals(ASTType o, Environment<ASTType> sigma) {
        return o instanceof ASTTArrow && ((ASTTArrow) o).getDom().defequals(dom, sigma)
            && ((ASTTArrow) o).getCodom().defequals(codom, sigma);
    }
}

