package proj.types;

import proj.ast.ASTNode;
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
        String domStr = (id == null) ? ""+dom : String.format("(%s:%s)", id, dom);
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

    public boolean defequals(ASTType o, Environment<ASTType> sigma, Environment<ASTNode> alphaL, Environment<ASTNode> alphaR) {
        return o instanceof ASTTLollipop olloli && dom.defequals(olloli.getDom(), sigma, alphaL, alphaR)
            && codom.defequals(olloli.getCodom(), sigma, alphaL, alphaR);
    }
}

