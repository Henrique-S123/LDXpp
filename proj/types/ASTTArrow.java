package proj.types;

import proj.ast.ASTNode;
import proj.env.*;
import proj.errors.*;

public class ASTTArrow extends ASTType {
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

    public void setSig(Env<ASTType> s) {
        sig = s;
        dom.setSig(s);
        codom.setSig(s);
    }

    public String toString() {
        String domStr = (id == null) ? ""+dom : String.format("(%s:%s)", id, dom);
        return String.format("%s->%s", domStr, codom);
    }

    public boolean isSubtypeOf(ASTType o, Env<ASTType> phi, AlphaEnv alpha) {
        if (o instanceof ASTTId) return isSubtypeOf(phi.unfold(o), phi, alpha);
        ASTType odom, ocodom;
        String oid;
        if (o instanceof ASTTArrow arrow) { odom = arrow.getDom(); ocodom = arrow.getCodom(); oid = arrow.getId(); }
        else if (o instanceof ASTTLollipop lolli) { odom = lolli.getDom(); ocodom = lolli.getCodom(); oid = lolli.getId(); }
        else return false;
        return odom.isSubtypeOf(dom, phi, alpha) && codom.isSubtypeOf(ocodom, phi, alpha.extend(id, oid));
    }

    public ASTType inst(String instId, ASTNode n) {
        return new ASTTArrow(dom.inst(instId, n), codom.inst(instId, n), id);
    }

    public ASTType check(Env<ASTType> sigma, Env<ASTType> phi) throws TypeCheckError, EnvironmentError {
        dom.check(sigma, phi);
        Env<ASTType> env = sigma.beginScope();
        env.assoc(id, dom);
        codom.check(sigma, phi);
        return this;
    }
}

