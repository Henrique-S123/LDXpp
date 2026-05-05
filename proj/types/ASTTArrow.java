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
        String domStr = (id == null) ? ""+dom : String.format("(%s:%s)", id, dom);
        return String.format("%s->%s", domStr, codom);
    }

    public boolean isSubtypeOf(ASTType o, EnvSet e) {
        if (o instanceof ASTTId) return isSubtypeOf(e.unfold(o), e);
        ASTType odom, ocodom;
        if (o instanceof ASTTArrow arrow) { odom = arrow.getDom(); ocodom = arrow.getCodom(); }
        else if (o instanceof ASTTLollipop lolli) { odom = lolli.getDom(); ocodom = lolli.getCodom(); }
        else return false;
        return odom.isSubtypeOf(dom, e) && codom.isSubtypeOf(ocodom, e);
    }

    public boolean defequals(ASTType o, Env<ASTType> sigma, AlphaEnv alpha) {
        return o instanceof ASTTArrow oarr && dom.defequals(oarr.getDom(), sigma, alpha)
            && codom.defequals(oarr.getCodom(), sigma, alpha.extend(id, oarr.getId()));
    }
}

