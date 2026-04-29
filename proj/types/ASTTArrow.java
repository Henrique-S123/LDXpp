package proj.types;

import proj.env.*;

public class ASTTArrow implements ASTType {
    ASTType dom, codom;
    String id;

    public ASTTArrow(ASTType d, ASTType co, String i) {
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

    public String toString() {
        String domStr = (id == null) ? dom.toString() : String.format("(%s:%s)", id, dom.toString());
        return domStr+"->"+codom.toString();
    }

    public boolean isSubtypeOf(ASTType o, EnvSet e) {
        if (o instanceof ASTTId) {
            ASTType to = e.unfold(o);
            return this.isSubtypeOf(to, e);
        }
        ASTType odom, ocodom;
        if (o instanceof ASTTArrow arrow) { odom = arrow.getDom(); ocodom = arrow.getCodom(); }
        else if (o instanceof ASTTLollipop lolli) { odom = lolli.getDom(); ocodom = lolli.getCodom(); }
        else return false;

        return odom.isSubtypeOf(dom, e) && codom.isSubtypeOf(ocodom, e);
    }

    public boolean defequals(ASTType o, Environment<ASTType> sigma) {
        return o instanceof ASTTArrow oarr && oarr.getDom().defequals(dom, sigma)
            && oarr.getCodom().defequals(codom, sigma);
    }
}

