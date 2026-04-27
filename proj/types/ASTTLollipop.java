package proj.types;

import proj.env.*;

public class ASTTLollipop implements ASTLinType {
    ASTType dom, codom;
    String id;

    public ASTTLollipop(ASTType d, ASTType co, String i) {
        dom = d;
        codom = co;
        id = i;
    }

    public ASTType getDom() {
        return dom;
    }

    public ASTType getCodom() {
        return codom;
    }

    public String getId() {
        return id;
    }

    public String toStr() {
        String domStr = (id == null) ? dom.toStr() : String.format("(%s:%s)", id, dom.toStr());
        return domStr+"-o>"+codom.toStr();
    }

    public boolean isSubtypeOf(ASTType o, EnvSet e) {
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

