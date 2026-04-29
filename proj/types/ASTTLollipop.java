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

    public String toString() {
        String domStr = (id == null) ? dom.toString() : String.format("(%s:%s)", id, dom);
        return String.format("%s-o>%s", domStr, codom);
    }

    public boolean isSubtypeOf(ASTType o, EnvSet e) {
        if (o instanceof ASTTId) {
            ASTType to = e.unfold(o);
            return this.isSubtypeOf(to, e);
        }
        ASTType odom, ocodom;
        if (o instanceof ASTTLollipop lolli) { odom = lolli.getDom(); ocodom = lolli.getCodom(); }
        else return false;
        return odom.isSubtypeOf(dom, e) && codom.isSubtypeOf(ocodom, e);
    }

    public boolean defequals(ASTType o, Environment<ASTType> sigma) {
        return o instanceof ASTTLollipop olloli && olloli.getDom().defequals(dom, sigma)
            && olloli.getCodom().defequals(codom, sigma);
    }
}

